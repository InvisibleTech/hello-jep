# Running a Detection

This project is a slightly improved version of a sample JAva application written to learn about
using https://github.com/ninia/jep to run models from https://huggingface.co/. The project is discussed in more detail
in this blog
post https://aninvisiblefriend.com/zero-shot-object-detection-using-java-python-and-hugging-face-28bd7f298c48.

## Installing the Python Libraries

Several attempts were made to get this project to run with the required JEP and PIL dependencies installed in a Python
environment. After spending some time with it, for just fell back to installing on the global Python. If you cannot find
a fix for this either but don't want to put JEP and PIL in your Python install then use a Python version manager and
make
one of those versions the one to which you do install these dependencies.

## Making the Executable

This project to some shortcuts for simplicity, one of which was to leverage the `maven-assembly-plugin` to create a
single jar. While it is not best practice to do so (due to various issues with dependency conflicts in uber-JAR etc),
for this simple sample application it is sufficient and it works.

Another simplifying assumption made here is that using the default behavior of `java.util.logging.Logger` without a
logging properties file will write INFO messages to the console.

To build the `jar` with dependencies:

```shell
 mvn package
```

## Example Execution Command Line

To run the detector after building it, the following command line template should help:

```shell
java \
 -Djava.library.path=/Users/johnferguson/.pyenv/versions/3.12.2/lib/python3.12/site-packages/jep \
 -jar target/hello-jep-1.0-SNAPSHOT-exe.jar \
 -s woman,'woman on the beach',surfboard,man,tree,'fall leaves' \
 /Users/johnferguson/work/Code/hugging_face/johnjohn_board.png

> class java.util.ArrayList
> [{score=0.850899338722229, box={ymin=153, xmin=117, ymax=501, xmax=748}, label=surfboard}, {score=0.34115299582481384, box={ymin=8, xmin=316, ymax=523, xmax=571}, label=man}]
```