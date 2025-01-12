package fr.greencodeinitiative.java.checks;
import java.util.*;


import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.model.declaration.VariableTreeImpl;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.plugins.java.api.tree.Tree.Kind;


@Rule(
        key = "CRJVM207-JAVA",
        name = "Developpement",
        description = CookieWithoutExpirationRule.MESSAGERULE,
        priority = Priority.MINOR,
        tags = {"smell"})

public class CookieWithoutExpirationRule extends IssuableSubscriptionVisitor {

    protected static final String MESSAGERULE = "Customer data must have end-of-life information, so cookies must have a maxAge";


 @Override
 public List<Tree.Kind> nodesToVisit() {
     return Collections.singletonList(Tree.Kind.CLASS);
 }
 private final CookieWithoutExpirationRuleCheckVisitor visitorInFile = new CookieWithoutExpirationRuleCheckVisitor();


 @Override
 public void visitNode(Tree tree) {

     tree.accept(visitorInFile);
     //Class visitor
     if (visitorInFile.hasANewCookieWithoutMaxDate())
     {
         //if we found a cookie that maxDate is not initialized, we report issue
         reportIssue(tree, "MaxAge must have a value");
     }
 }

    private class CookieWithoutExpirationRuleCheckVisitor extends BaseTreeVisitor {


        // storage of variable name for setMaxAge
        private ArrayList<String> hasSetMaxAgeForCookiesVariableName = new ArrayList<>();
        // storage of variable name for New Cookies
        private ArrayList<String>  newCookieVariableName = new ArrayList<>();
        @Override
        public void visitReturnStatement(ReturnStatementTree tree) {
            this.scan(tree.expression());
        }

        @Override
        public void visitVariable(VariableTree tree) {
            //when we visit variable affectation
            for (Tree children : ((VariableTreeImpl) tree).children())
            {
                //if we found an affectation
                if (children.is(Tree.Kind.NEW_CLASS)
                        && ((IdentifierTree)((NewClassTree)children).identifier()).toString().equals("Cookie"))
                {
                    //if this is a New Cookie affectation, we store the name of the variable
                    this.newCookieVariableName.add(tree.simpleName().toString());
                }
                else
                    super.visitVariable(tree);
            }
        }


        public boolean hasANewCookieWithoutMaxDate()
        {
            //loop on variables on which a new Cookie was done
            for (String variableName : newCookieVariableName )
            {
                if (!hasSetMaxAgeForCookiesVariableName.contains(variableName))
                    //no setMaxAge has been done
                    return true;
            }
            return false;
        }
        @Override
        public void visitMethodInvocation(MethodInvocationTree tree) {
            //Method visiting
            if (tree.methodSelect().is(Kind.MEMBER_SELECT) &&
                    (((MemberSelectExpressionTree)tree.methodSelect()).identifier().name().equals("setMaxAge"))
            )
            {
                    //if visited methode is setMaxAge, then save the assigned variable
                    hasSetMaxAgeForCookiesVariableName.add(((MemberSelectExpressionTree)tree.methodSelect()).expression().toString());
            }

        }


    }
}