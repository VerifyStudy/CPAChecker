package org.sosy_lab.cpachecker.cfa.ast.c;

import java.util.logging.Level;

import org.sosy_lab.common.log.LogManager;

public class MyCExpressionVisitor extends DefaultCExpressionVisitor<Void, RuntimeException> {

  private final LogManager logger;
  private String ast;

  public MyCExpressionVisitor(LogManager pLpgger) {
    logger = pLpgger;

  }

  @Override
  protected Void visitDefault(CExpression pExp) throws RuntimeException {
    logger.log(Level.INFO, pExp.toASTString().toString());
    logger.log(Level.INFO, pExp.getExpressionType());
    ast = pExp.toASTString().toString();
    return null;
  }

  @Override
  public Void visit(CBinaryExpression e) throws RuntimeException {
    // 创建
    logger.log(Level.INFO, e.toASTString().toString());
    logger.log(Level.INFO, e.getOperand1());
    logger.log(Level.INFO, e.getOperand2());
    logger.log(Level.INFO, e.getOperator());
    return null;
  }

  public String getAst() {
    return this.ast;
  }
}
