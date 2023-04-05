package fr.greencodeinitiative.java.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.MethodMatchers;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

import static fr.greencodeinitiative.java.checks.ConstOrLiteralDeclare.isLiteral;
import static java.util.Arrays.asList;
import static org.sonar.plugins.java.api.semantic.Type.Primitives.INT;
import static org.sonar.plugins.java.api.tree.Tree.Kind.MEMBER_SELECT;
import static org.sonar.plugins.java.api.tree.Tree.Kind.METHOD_INVOCATION;

@Rule(key = "CRJVM205", name = "Developpement",
        description = ForceUsingLazyFetchTypeInJPAEntity.MESSAGERULE,
        priority = Priority.MINOR,
        tags = {"bug"})

public class ForceUsingLazyFetchTypeInJPAEntity extends IssuableSubscriptionVisitor {

    protected static final String MESSAGERULE = "Force the use of LAZY FetchType";

    @Override
    public List<Kind> nodesToVisit() {
        return Arrays.asList(Kind.VARIABLE);
    }

    @Override
    public void visitNode(Tree tree) {

        if (tree.is(Kind.VARIABLE)) {
            VariableTree variableTree = (VariableTree) tree;
            List<AnnotationTree> annotations = variableTree.modifiers().annotations();

            for(AnnotationTree annotationTree : annotations){
                System.out.print(annotationTree.annotationType());

                if("OneToMany".equals(annotationTree.annotationType().toString())||"ManyToOne".equals(annotationTree.annotationType())){

                    Arguments arguments = annotationTree.arguments();
                    for (ListIterator<ExpressionTree> it = arguments.listIterator(); it.hasNext(); ) {
                        ExpressionTree argument = it.next();
                        AssignmentExpressionTree assignementExpression = (AssignmentExpressionTree)argument;

                        IdentifierTree variable = (IdentifierTree) assignementExpression.variable();
                        if("fetch".equals(variable.name())){
                            //--attribut
                            String fetchValue = ((MemberSelectExpressionTree)assignementExpression.expression()).identifier().name();
                            System.out.print(fetchValue);
                            if("EAGER".equals(fetchValue)){
                                reportIssue(tree, MESSAGERULE);
                            }
                        }

                    }

                }
            }

        }
    }


}
