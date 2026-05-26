# Map Interface: HashMap vs TreeMap

## 1. Introduction
I'm going to talk about the Map interface, this is essential in Java for storing key-value pairs (Oracle, n.d.-b), there are different ways to do this, 2 of the most used are HashMap and TreeMap (Oracle, n.d.-b), both work completely differently, and you also use them in different situations.

Then we also get to my **Main Question:** How do HashMap and TreeMap differ technically, and when do you choose which implementation?

**Sub-questions:**
1. What is the Map interface?
2. How does HashMap work internally?
3. How does TreeMap work internally?
4. When do you use which?

---

## 2. The Map Interface
The Map interface stores data in key-value pairs, each key is unique, and refers to max 1 value (Oracle, n.d.-b). The most important methods are:
1. `put()` for adding (Oracle, n.d.-b)
2. `get()` for retrieving (Oracle, n.d.-b)
3. `remove()` for deleting (Oracle, n.d.-b)

Map has different implementations (Oracle, n.d.-b):
- HashMap: fast and unordered (Oracle, n.d.-a)
- TreeMap: slower but ordered (Oracle, n.d.-d)

---

## 3. HashMap: Hashing for Speed

### The Data Structure: Hash Table
HashMap uses a hash table as its data structure (Oracle, n.d.-a). This is an array where you can calculate directly where something needs to be stored (Baeldung, 2023).

### The Algorithm: Hashing
HashMap uses index hashing, this means that each key is converted to a number via `hashCode()` (Oracle, n.d.-c). This number determines, with modulo, the array index (GeeksforGeeks, n.d.-b), this ensures direct calculation of where data is stored without searching. This results in O(1) time complexity for basic operations like get and put (Oracle, n.d.-a).

Collisions can also occur, this means that two keys get the same index (Baeldung, 2023). Java fortunately has a solution for this namely chaining, where each array position contains a linked list (GeeksforGeeks, n.d.-b). The load factor determines when the array gets bigger, so that there are fewer collisions (Oracle, n.d.-a). The default load factor is 0.75, which offers a good tradeoff between time and space costs (Oracle, n.d.-a). This has advantages and disadvantages:

**Advantages:**
- Extremely fast: O(1) operations on average (Oracle, n.d.-a)
- Efficient for lookups (Baeldung, 2025)
- Null keys allowed (Oracle, n.d.-a)

**Disadvantages:**
- No order guaranteed (Oracle, n.d.-a)
- Memory overhead with resizing (Oracle, n.d.-a)

Some use possibilities for HashMap (Baeldung, 2025):
- Cache systems
- User sessions

In situations where speed is the most important.

---

## 4. TreeMap: Red-Black Tree for Sorting

### The Data Structure: Red-Black Tree
TreeMap works just a bit differently, it uses a Red-Black tree, a balanced binary search tree as its data structure (Oracle, n.d.-d). The tree always stays in balance, which means that the height is always log(n) (GeeksforGeeks, n.d.-a), this again ensures O(log n) time complexity for operations like containsKey, get, put and remove (Oracle, n.d.-d).

### The Algorithm: Self-Balancing
The Red-Black tree uses a balancing algorithm that automatically keeps the tree balanced through rotations (GeeksforGeeks, n.d.-a). This ensures that searching always remains efficient. Each node is colored either red or black (GeeksforGeeks, n.d.-a), and specific properties ensure the tree remains approximately balanced: the root is always black, red nodes cannot have red children, and every path from a node to its descendant leaves contains the same number of black nodes (GeeksforGeeks, n.d.-a).

The biggest advantage of TreeMap is that it automatically sorts, keys always stay sorted in natural order (Int from low to high, strings alphabetically) (Oracle, n.d.-d). TreeMap also has, just like HashMap, special navigation methods like (Oracle, n.d.-d):
- `firstKey()` - Gives the first key
- `lastKey()` - Gives the last key
- `subMap()` - Gives all keys between 2 values

This has a number of advantages and disadvantages:

**Advantages:**
- Automatically sorted (Oracle, n.d.-d)
- Range queries possible (Oracle, n.d.-d)
- Predictable O(log n) performance (Oracle, n.d.-d)

**Disadvantages:**
- Slower than HashMap due to log(n) complexity (Oracle, n.d.-d)
- No null keys allowed (Oracle, n.d.-d)
- More memory per entry due to tree node overhead (Oracle, n.d.-d)

You use TreeMap in for example leaderboards, timelines, scheduled tasks, in short situations where sorting is essential.

---

## 5. Practical Example: Code Demonstration

The following examples demonstrate the key difference between HashMap and TreeMap (Baeldung, 2025).

### HashMap Example
```java
Map<String, Integer> hashMap = new HashMap<>();
hashMap.put("Charlie", 25);
hashMap.put("Alice", 30);
hashMap.put("Bob", 20);

System.out.println(hashMap.keySet());
// Output: [Bob, Alice, Charlie] random order!
```

HashMap does not guarantee any specific order of the keys (Oracle, n.d.-a).

### TreeMap Example
```java
Map<String, Integer> treeMap = new TreeMap<>();
treeMap.put("Charlie", 25);
treeMap.put("Alice", 30);
treeMap.put("Bob", 20);

System.out.println(treeMap.keySet());
// Output: [Alice, Bob, Charlie] alphabetically sorted!

// TreeMap extra features:
treeMap.firstKey();        //  Output: "Alice"
treeMap.lastKey();         //  Output: "Charlie"
treeMap.subMap("A", "C");  //  Output: Range between A and C
```

TreeMap maintains keys in sorted order according to their natural ordering (Oracle, n.d.-d). This example shows the difference very well: HashMap gives the key pairs in random order, and TreeMap automatically sorted. TreeMap also provides additional navigation methods that HashMap doesn't have (Oracle, n.d.-d).

---

## 6. Comparison and Choice

| Aspect | HashMap | TreeMap |
|--------|---------|---------|
| Data Structure | Hash Table (Oracle, n.d.-a) | Red-Black Tree (Oracle, n.d.-d) |
| Algorithm | Hashing (Oracle, n.d.-a) | Self-Balancing (GeeksforGeeks, n.d.-a) |
| Time Complexity | O(1) (Oracle, n.d.-a) | O(log n) (Oracle, n.d.-d) |
| Order | None (Oracle, n.d.-a) | Sorted (Oracle, n.d.-d) |
| Null keys | Yes (Oracle, n.d.-a) | No (Oracle, n.d.-d) |
| Use when | Speed priority | Sorting needed |

As you also see in the table again, it's best to use HashMap when it's about speed, if you need sorting you can better use TreeMap (Baeldung, 2025). In practice you almost always use HashMap, because of the speed (Baeldung, 2025). You only use TreeMap in specific use cases where you need sorted data (Oracle, n.d.-d).

---

## 7. Conclusion & What I Learned From It

**Answer to main question:**
HashMap and TreeMap are completely different, both in how you use it and in performance. HashMap uses hashing (hash table data structure) for O(1) speed (Oracle, n.d.-a), but doesn't sort. TreeMap uses Red-Black Tree (tree data structure with balancing algorithm) for O(log n) (Oracle, n.d.-d; GeeksforGeeks, n.d.-a) which automatically sorts it.

In one sentence: **Choose HashMap for speed, choose TreeMap for sorting.**

Through this research I now understand that there is no "best" map implementation, it really depends on the situation you're in, when I have to use key-value pair in the future I now know that I have to look at the situation, and then it will quickly become clear which one I need.

- **Do I need pure speed?** → Then I choose HashMap (Oracle, n.d.-a)
- **Must my data stay sorted?** → Then I choose TreeMap (Oracle, n.d.-d)
- **Do I need range queries?** → Then I choose TreeMap (Oracle, n.d.-d)
- **Does order not matter?** → Then I choose HashMap (the default) (Oracle, n.d.-a)

I think it's important that I learned this because it shows that software engineering doesn't only revolve around writing brilliant code but also about realizing the situation you're in, and basing on that what you need.

---

## References

Baeldung. (2023, November 6). *HashMap under the hood*. https://www.baeldung.com/java-hashmap-advanced

Baeldung. (2025, January 15). *A guide to HashMap in Java*. https://www.baeldung.com/java-hashmap

GeeksforGeeks. (n.d.-a). *Introduction to Red-Black tree*. https://www.geeksforgeeks.org/introduction-to-red-black-tree/

GeeksforGeeks. (n.d.-b). *Internal working of HashMap in Java*. https://www.geeksforgeeks.org/internal-working-of-hashmap-java/

Oracle. (n.d.-a). *Class HashMap<K,V>*. Java Platform, Standard Edition 8 API Specification. https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html

Oracle. (n.d.-b). *Interface Map<K,V>*. Java Platform, Standard Edition 8 API Specification. https://docs.oracle.com/javase/8/docs/api/java/util/Map.html

Oracle. (n.d.-c). *Object.hashCode()*. Java Platform, Standard Edition 8 API Specification. https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#hashCode--

Oracle. (n.d.-d). *Class TreeMap<K,V>*. Java Platform, Standard Edition 8 API Specification. https://docs.oracle.com/javase/8/docs/api/java/util/TreeMap.html