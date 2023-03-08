package fr.greencodeinitiative.php.checks;

import com.google.re2j.Pattern;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.php.api.tree.declaration.FunctionDeclarationTree;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

@Rule(
        key = "D4",
        name = "Developpement",
        description = AvoidUsingGlobalVariablesCheck.ERROR_MESSAGE,
        priority = Priority.MINOR,
        tags = {"bug"})

public class AvoidUsingGlobalVariablesCheck extends PHPVisitorCheck {

    public static final String ERROR_MESSAGE = "Prefer local variables to globals";

    private static final Pattern PATTERN = Pattern.compile("^.*(global \\$|\\$GLOBALS).*$", Pattern.CASE_INSENSITIVE);

    @Override
    public void visitFunctionDeclaration(FunctionDeclarationTree tree) {
        if (PATTERN.matcher(tree.body().toString()).matches()) {
            context().newIssue(this, tree, String.format(ERROR_MESSAGE, tree.body().toString()));
        }
    }
}
