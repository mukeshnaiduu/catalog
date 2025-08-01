mkdir -p target/classes
javac -d target/classes -cp "." src/main/java/com/catalog/SecretFinder.java
java -cp target/classes com.catalog.SecretFinder
