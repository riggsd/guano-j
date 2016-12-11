guano-j
-------

Java reference implementation of the [GUANO metadata specification](https://github.com/riggsd/guano-py/blob/master/doc/guano_specification.md).

Don't use this code yet!


Example Usage
=============

```java
    import guano.GuanoReader

    try {
        GuanoReader reader = new GuanoReader("test.wav");
        reader.getFloat("GUANO", "Version")
        reader.get("Species Auto ID")
    catch (IOException e) {
        e.printStackTrace();
    }
```

Installation
============

    $> git clone https://github.com/riggsd/guano-j.git
    $> cd guano-j
    $> ant

Seriously, don't use this code yet!
