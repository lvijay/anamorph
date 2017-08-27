Cylindrical Anamorpher
======================

A simple program to compute the cylindrical anamorph of a given
image.  For usage instructions, run without arguments.

The reference source for this implementation is based on the power
point at
http://facultyfp.salisbury.edu/despickler/personal/Resources/TechnologyWorkshops/ScienceNight2011/ScienceNightSU.pdf

## Usage

Requires Java 8 or above.

To compile the program

    mkdir bin
    find src -type f -name '*.java' -exec javac -Xlint:all -cp src -d bin {} \+

To run

    java -cp bin anam.main.AnamorphImage

And run again with the instructions.

For license information see the file COPYING.md
