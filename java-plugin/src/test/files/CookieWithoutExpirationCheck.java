import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

class TestClass {// Noncompliant {{MaxAge must have a value}}

	public void NOK_CookieCreation() {
		// create objet cookie
		Cookie A = new Cookie("id","674684641");
	}

	public Cookie NOK_ReturnCookieCreation() {
		// create objet cookie
		Cookie A = new Cookie("id","674684641");
		return  A;
	}
	public void OK_CookieCreation() {
		// create objet cookie
		Cookie B = new Cookie("id","674684641");
		// set the validity
		B.setMaxAge(24*3600);

	}

	public void OK_CookieCreation2() {
		// create objet cookie
		Cookie C;
		C = new Cookie("id","674684641");
		// set the validity
		C.setMaxAge(24*3600);

	}

}