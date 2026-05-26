# Coding Conventions – StemOpFeiten

### Why have coding conventions?

When working on a project with multiple developers, differences in style and structure can quickly lead to confusion and inconsistency. Coding conventions provide a shared set of rules that ensure all code looks and feels the same, regardless of who wrote it.

They are essential because they:

* **Improve readability** – making it easier for team members to understand each other’s code.
* **Increase maintainability** – ensuring the project can be updated and extended without unnecessary complexity.
* **Support collaboration** – creating a predictable structure that helps new developers contribute faster.
* **Reduce errors** – minimizing misunderstandings and inconsistencies that could introduce bugs.

By following coding conventions, the team creates a clean, professional, and efficient codebase that can grow sustainably.

---

## 1. General Guidelines

* **Consistency first**: Always follow the agreed style, even if you prefer another.
* **Clarity over cleverness**: Code should be easy to read and maintain.
* **One responsibility per unit**: Classes, functions, and modules should do one thing well.
* **Avoid duplication**: Use reusable methods/components.
* **Document decisions**: Comment on *why*, not *what* (the code already shows what).

---

## 2. Naming Conventions
We follow the naming conventions that oracle gives us (on Code Conventions For The Java Programming Language: 9. Naming Conventions, z.d).

### Java

* **Classes & Interfaces**: `PascalCase`

  ```java
  public class StudentManager {
      // ...
  }

  public interface PaymentService {
      void processPayment ();
  }
  ```

* **Methods & Variables**: `camelCase`

  ```java
  public double calculateTotal (double price, double taxRate) {
      double totalPrice = price + (price * taxRate);
      return totalPrice;
  }
  ```

* **Constants**: `UPPER_SNAKE_CASE`

  ```java
  public static final int MAX_VALUE = 100;
  ```


---

## 3. Formatting

* **Indentation**: 4 spaces (Code Conventions For The Java Programming Language: 4. Indentation, z.d.).
* **Braces**: braces should be added to end of statement with one indentation in between
* **Curly braces**: Curly braces should start the same line as the statement with one indentation in between, then on a new line the body should start and after that close the statement with a closing curly brace on a new line (Code Conventions For The Java Programming Language: 7. Statements, z.d.).

  ```java
  public String foo () {
      return "foo";
  }
  ```

---

## 4. Code Structure

* One public class per file 
* Keep methods short (a method should focus on one concept)
* Order: **fields → constructors → methods**
* Use Javadoc (`/** ... */`) for public methods/classes

Example:

```java
/**
 * Use Javadoc to comment for public classes and methods
 */
public class StudentManager 
{
    private List<String> students;

    public StudentManager () {
        this.students = new ArrayList<>();
    }

    /**
    * Use Javadoc to comment for public classes and methods
    */
    public void addStudent (String name) 
    {
        students.add(name);
    }

    public List<String> getStudents () 
    {
        return students;
    }
}
```

---

## 5. Comments & Documentation

* Use Javadoc to explain **why**, not **what**, at the **method level only** (Propedeuse, z.d.).

### ❌ Incorrect Example (describes *what*)

```java
public class PaymentProcessor {

    /**
     * Adds a payment to the balance.
     * @param amount The payment amount
     */
    public void process (double amount) {
        addToBalance(amount);
        logTransaction(amount);
    }

    private void addToBalance (double amount) { /* ... */ }
    private void logTransaction (double amount) { /* ... */ }
}
```

*Problem*: The comment just restates what the code does. It doesn’t explain **why** this logic exists.

---

### ✅ Correct Example (explains *why*)

```java
public class PaymentProcessor {

    /**
     * Processes a payment only if it exceeds the minimum threshold.
     * This avoids processing micro-payments where fees could exceed the payment value.
     * Also ensures thread-safety when updating the balance.
     *
     * @param amount The payment amount
     */
    public void process (double amount) {
        if (amount < 1.0) {
            return;
        }

        synchronized (this) {
            addToBalance(amount);
        }

        logTransaction(amount);
    }

    private void addToBalance(double amount) { /* ... */ }
    private void logTransaction(double amount) { /* ... */ }
}
```

### Language
Last but not least. **Everything** should be in **English**. This is strictly for the code, comments and documentation. not for text shown to the user.

---

### Sources

- Code Conventions for the Java Programming Language: 4. Indentation. (z.d.). https://www.oracle.com/java/technologies/javase/codeconventions-indentation.html

- Code Conventions for the Java Programming Language: 9. Naming Conventions. (z.d.). https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html

- Code Conventions for the Java Programming Language: 7. Statements. (z.d.). https://www.oracle.com/java/technologies/javase/codeconventions-statements.html#:~:text=Braces%20are%20used%20around%20all,to%20forgetting%20to%20add%20braces.

- Propedeuse, T. (z.d.). Code commentaar - Knowledgebase. https://knowledgebase.hbo-ict-hva.nl/1_beroepstaken/software/realiseren/code_conventies/code_commentaar/#doc-comments

