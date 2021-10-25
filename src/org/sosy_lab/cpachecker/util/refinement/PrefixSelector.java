// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2007-2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.util.refinement;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.sosy_lab.cpachecker.util.LoopStructure;
import org.sosy_lab.cpachecker.util.variableclassification.VariableClassification;

public class PrefixSelector {

	private final ScorerFactory factory;
	private final Optional<VariableClassification> classification;

	public PrefixSelector(
			Optional<VariableClassification> pClassification,
			Optional<LoopStructure> pLoopStructure) {
		factory = new ScorerFactory(pClassification, pLoopStructure);
		classification = pClassification;
	}

	public InfeasiblePrefix selectSlicedPrefix(
			List<PrefixPreference> pPrefixPreference,
			List<InfeasiblePrefix> pInfeasiblePrefixes) {
		return Ordering.compound(createComparators(pPrefixPreference)).min(pInfeasiblePrefixes);
	}

	public int obtainScoreForPrefixes(
			final List<InfeasiblePrefix> pPrefixes,
			final PrefixPreference pPreference) {

		if (!classification.isPresent()) {
			return Scorer.DEFAULT_SCORE;
		}

		Scorer scorer = factory.createScorer(pPreference);
		return pPrefixes.stream()
				.mapToInt(p -> scorer.computeScore(p))
				.min()
				.orElse(Scorer.DEFAULT_SCORE);
	}

	private List<Comparator<InfeasiblePrefix>>
			createComparators(List<PrefixPreference> pPrefixPreference) {
		return Lists.transform(pPrefixPreference, p -> factory.createScorer(p).getComparator());
	}

	// 静态调用PrefixSelector.NO_SELECTION返回NONE优先级
	public static final List<PrefixPreference> NO_SELECTION =
			Collections.singletonList(PrefixPreference.NONE);

	public enum PrefixPreference {

		// 基于反例路径前缀长度的启发式算法
		LENGTH_MIN,
		LENGTH_MAX,

		// 基于插值序列中变量的域类型评分的启发式算法
		DOMAIN_MIN,
		DOMAIN_MAX,

		// 基于内插序列中引用的循环计数器变量的启发式算法
		LOOPS_MIN,
		LOOPS_MAX,

		// 基于近似求精根深度的启发式算法
		PIVOT_MIN,
		PIVOT_MAX,

		// 基于插值序列宽度的启发式算法
		WIDTH_MIN,
		WIDTH_MAX,

		// 基于计算插值序列中变量的 赋值数量 的启发式算法
		ASSIGNMENTS_MIN,
		ASSIGNMENTS_MAX,

		// 基于计算插值序列中变量的 假设数 的启发式算法
		ASSUMPTIONS_MIN,
		ASSUMPTIONS_MAX,

		// 随机
		RANDOM,

		// 不执行任何选择
		NONE
	}

	private static class ScorerFactory {

		private final Optional<VariableClassification> classification;
		private final Optional<LoopStructure> loopStructure;

		// We instantiate this only once, because it is pseudo-random (i.e., deterministic),
		// and if we instantiate it every time, we get the same sequence of random numbers.
		private final Scorer randomScorer = new RandomScorer();

		public ScorerFactory(
				final Optional<VariableClassification> pClassification,
				final Optional<LoopStructure> pLoopStructure) {
			classification = pClassification;
			loopStructure = pLoopStructure;
		}

		public Scorer createScorer(PrefixPreference pPreference) {
			switch (pPreference) {
				case LENGTH_MIN:
					return new LengthScorer();
				case LENGTH_MAX:
					return new LengthScorer().invert();
				case DOMAIN_MIN:
					return new DomainScorer(classification, loopStructure);
				case DOMAIN_MAX:
					return new DomainScorer(classification, loopStructure).invert();
				case LOOPS_MIN:
					return new LoopScorer(classification, loopStructure);
				case LOOPS_MAX:
					return new LoopScorer(classification, loopStructure).invert();
				case WIDTH_MIN:
					return new WidthScorer();
				case WIDTH_MAX:
					return new WidthScorer().invert();
				case PIVOT_MIN:
					return new DepthScorer();
				case PIVOT_MAX:
					return new DepthScorer().invert();
				case ASSIGNMENTS_MIN:
					return new AssignmentScorer(classification);
				case ASSIGNMENTS_MAX:
					return new AssignmentScorer(classification).invert();
				case ASSUMPTIONS_MIN:
					return new AssumptionScorer(classification);
				case ASSUMPTIONS_MAX:
					return new AssumptionScorer(classification).invert();
				case RANDOM:
					return randomScorer;

				// illegal arguments
				case NONE:
				default:
					throw new IllegalArgumentException(
							"Illegal prefix preference " + pPreference + " given!");
			}
		}
	}

	private interface Scorer {
		static final int DEFAULT_SCORE = Integer.MAX_VALUE;

		int computeScore(final InfeasiblePrefix pPrefix);

		default Comparator<InfeasiblePrefix> getComparator() {
			return Comparator.comparingInt(this::computeScore);
		}

		default Scorer invert() {
			Scorer delegate = this;
			return new Scorer() {
				@Override
				public int computeScore(InfeasiblePrefix pPrefix) {
					int score = delegate.computeScore(pPrefix);
					if (score == Integer.MIN_VALUE) {
						return DEFAULT_SCORE;
					}
					return -score;
				}
			};
		}
	}

	private static class DomainScorer implements Scorer {

		private final Optional<VariableClassification> classification;
		private final Optional<LoopStructure> loopStructure;

		public DomainScorer(
				final Optional<VariableClassification> pClassification,
				final Optional<LoopStructure> pLoopStructure) {
			classification = pClassification;
			loopStructure = pLoopStructure;
		}

		@Override
		public int computeScore(final InfeasiblePrefix pPrefix) {
			return classification.orElseThrow()
					.obtainDomainTypeScoreForVariables(
							pPrefix.extractSetOfIdentifiers(),
							loopStructure);
		}
	}

	private static class LoopScorer implements Scorer {

		private final Optional<VariableClassification> classification;
		private final Optional<LoopStructure> loopStructure;

		public LoopScorer(
				final Optional<VariableClassification> pClassification,
				final Optional<LoopStructure> pLoopStructure) {
			classification = pClassification;
			loopStructure = pLoopStructure;
		}

		@Override
		public int computeScore(final InfeasiblePrefix pPrefix) {
			int score =
					classification.orElseThrow()
							.obtainDomainTypeScoreForVariables(
									pPrefix.extractSetOfIdentifiers(),
									loopStructure);

			// TODO next line looks like a bug. The score is either MAX_INT or ZERO afterwards.
			if (score != DEFAULT_SCORE) {
				score = 0;
			}

			return score;
		}
	}

	private static class WidthScorer implements Scorer {

		@Override
		public int computeScore(final InfeasiblePrefix pPrefix) {
			return pPrefix.getNonTrivialLength();
		}
	}

	// 可用插值的长度
	// 距离错误结点的距离
	private static class DepthScorer implements Scorer {

		@Override
		public int computeScore(final InfeasiblePrefix pPrefix) {
			return pPrefix.getDepthOfPivotState();
		}
	}

	// 路径长度，可能会切出许多条路径
	private static class LengthScorer implements Scorer {

		@Override
		public int computeScore(final InfeasiblePrefix pPrefix) {
			return pPrefix.getPath().size();
		}
	}

	private static class AssignmentScorer implements Scorer {

		private final Optional<VariableClassification> classification;

		public AssignmentScorer(final Optional<VariableClassification> pClassification) {
			classification = pClassification;
		}

		@Override
		public int computeScore(final InfeasiblePrefix pPrefix) {
			int count = 0;
			for (String variable : pPrefix.extractSetOfIdentifiers()) {
				count = count + classification.orElseThrow().getAssignedVariables().count(variable);
			}

			return count;
		}
	}

	private static class AssumptionScorer implements Scorer {

		private final Optional<VariableClassification> classification;

		public AssumptionScorer(final Optional<VariableClassification> pClassification) {
			classification = pClassification;
		}

		@Override
		public int computeScore(final InfeasiblePrefix pPrefix) {
			int count = 0;
			for (String variable : pPrefix.extractSetOfIdentifiers()) {
				count = count + classification.orElseThrow().getAssumedVariables().count(variable);
			}

			return count;
		}
	}

	private static class RandomScorer implements Scorer {

		private static final Random random = new Random(0);

		@Override
		public int computeScore(final InfeasiblePrefix pPrefix) {
			return random.nextInt(1000);
		}
	}

}
