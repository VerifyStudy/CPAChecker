// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2007-2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.cpa.testtargets;

import org.sosy_lab.cpachecker.core.defaults.SimplePrecisionAdjustment;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.core.interfaces.PrecisionAdjustmentResult.Action;
import org.sosy_lab.cpachecker.exceptions.CPAException;
import org.sosy_lab.cpachecker.util.AbstractStates;

public class TestTargetPrecisionAdjustment extends SimplePrecisionAdjustment {

  @Override
  public Action prec(final AbstractState pState, final Precision pPrecision) throws CPAException {
    TestTargetState tState = AbstractStates.extractStateByType(pState, TestTargetState.class);
    return tState.isTarget() ? Action.BREAK : Action.CONTINUE;
  }
}
