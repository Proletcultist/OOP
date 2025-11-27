 #!/bin/bash
    JAR_FILE="build/libs/Task_1_4_1.jar"

    # Loop through all .class files in the JAR
    for class_path in $(jar -tf "$JAR_FILE" | grep '\.class$'); do
        # Convert file path to full class name (e.g., com/example/MyClass.class to com.example.MyClass)
        class_name=$(echo "$class_path" | sed 's/\.class$//' | sed 's/\//\./g')

        echo "--- Methods in class: $class_name ---"
        # Use javap to list methods for the class, specifying the JAR as the classpath
        javap -classpath "$JAR_FILE" "$class_name"
        echo ""
    done

