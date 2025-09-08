#!/bin/bash

SRC_DIR="src/main/java"
CLASSES_DIR="build/classes/java/main"
JAR_DIR="build/libs"
LIB_NAME="Task_1_1_1"
JAVADOC_DIR="build/docs/javadoc"

# Create main classes output dir if not exist
if [ ! -d "$CLASSES_DIR" ]; then
	mkdir -p "$CLASSES_DIR"
fi

# Compile org.example.util.* classes
javac "$SRC_DIR"/org/example/util/* -d "$CLASSES_DIR"

# Create lib output dir if not exist
if [ ! -d "$JAR_DIR" ]; then
	mkdir -p "$JAR_DIR"
fi

# Create jar archive with all compiled classes
jar -cf "$JAR_DIR/$LIB_NAME.jar" -C "$CLASSES_DIR" .

# Create javadoc output dit if not exist
if [ ! -d "$JAVADOC_DIR" ]; then
	mkdir -p "$JAVADOC_DIR"
fi

# Generate javadoc for org.example.util.* classes
javadoc "$SRC_DIR"/org/example/util/* -d "$JAVADOC_DIR"
