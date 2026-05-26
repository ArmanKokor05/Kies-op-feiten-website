

---

# **Research Report – List Interface, ArrayList & LinkedList**

**Name:** *Monsif Lanjri*
**Date:** *11-11-2025*

---

## **1. Introduction**

For this research, I studied the **List interface** in the Java Collection Framework and two of its most common implementations: **ArrayList** and **LinkedList**.
My goal was to understand how the List interface works, how these two classes store data internally, and when each one is the better choice.

---

## **2. Problem Statement**

Developers often use lists in Java, but many students do not fully understand the differences between ArrayList and LinkedList.
The core problem is: *How do these data structures work internally, and how does their structure affect performance?*

---

## **3. Main Research Question**

**How do ArrayList and LinkedList differ in structure and performance within the Java Collection Framework?**

---

## **4. Sub-questions**

To answer the main question, I created the following sub-questions:

1. What is the List interface in Java?
2. How does an ArrayList store data internally?
3. How does a LinkedList store data internally?
4. Which operations are faster or slower?
5. When should each implementation be used?

---

## **5. Theory & Explanation**

### **5.1 The List Interface**

The List interface defines what methods a list *must* support, such as:

* `add()`
* `remove()`
* `get()`
* `set()`
* `size()`
* `clear()`

It describes **what** a list must do, but not **how** the operations are implemented.
ArrayList and LinkedList both follow these rules in different ways.

---

### **5.2 ArrayList – Internal Structure**

ArrayList uses a **dynamic array**, which means all elements are stored **next to each other in memory**.

**Advantages:**

* Very fast access by index
* Fast when adding at the end

**Disadvantages:**

* Slow when inserting or removing in the middle
* Requires shifting many elements

---

### **5.3 LinkedList – Internal Structure**

LinkedList stores elements in **nodes**.
Each node contains the data and a reference to the next node.

**Advantages:**

* Fast insertion and removal, especially at the beginning or middle
* No shifting of elements

**Disadvantages:**

* Very slow access by index (must traverse node by node)
* Uses more memory due to pointers

---

## **6. Performance Test**

### **6.1 Test Setup**

I wrote a small Java demo to compare the performance of ArrayList and LinkedList.
I measured three operations:

1. Adding **100,000** elements at the end
2. Reading the **middle element** 100,000 times
3. Adding an element to **index 0** ten thousand times

The measurement was done using `System.nanoTime()`.

---

### **6.2 Results (pattern)**

Even though the exact numbers can change per run, the pattern is always the same:

| **Operation**       | **ArrayList** | **LinkedList** |
| ------------------- | ------------- | -------------- |
| Add 100,000 at end  | Faster        | Slower         |
| Get middle 100,000× | Much faster   | Much slower    |
| Add 10,000 at start | Slower        | Faster         |

---

## **7. Analysis**

The performance differences are explained by the internal structure:

### **ArrayList**

* Fast reading because items are next to each other
* Slow modification in the middle because elements must shift

### **LinkedList**

* Fast modifications because only pointers change
* Slow reading because it must walk through every node to reach the target

Neither structure is strictly “better”; each is optimized for different operations.

---

## **8. Conclusion**

This research shows that:

* **ArrayList** is best for fast access and adding at the end.
* **LinkedList** is best when you frequently insert or remove elements, especially near the beginning.
* The correct choice depends on the situation and the operations needed in your program.

I now understand much more clearly how internal data structures affect performance in the Java Collection Framework.

---

## **9. Sources (APA 7)**

Oracle. (2025). *Java Platform, Standard Edition Documentation*. Oracle Corporation.
[https://docs.oracle.com/javase/](https://docs.oracle.com/javase/)

GeeksforGeeks. (2025). *Difference Between ArrayList and LinkedList in Java*.
[https://www.geeksforgeeks.org/difference-between-arraylist-and-linkedlist-in-java/](https://www.geeksforgeeks.org/difference-between-arraylist-and-linkedlist-in-java/)

Baeldung. (2025). *Guide to the Java List Interface*.
[https://www.baeldung.com/java-list](https://www.baeldung.com/java-list)

Oracle. (2025). *Java Collections Framework Overview*. Oracle Corporation.
[https://docs.oracle.com/javase/tutorial/collections/intro/index.html](https://docs.oracle.com/javase/tutorial/collections/intro/index.html)

---


