# Usage

Input and output formats are CSV (separated by '\t'). It joins two files into one. Each row in the output file is a concatenation of two rows respectively from the two input files, which have the same value on the specified columns.

# Example

Input1:

a	1	true  
b	2	false  
c	3	true  

Input2:

1	sam  
2	john  

Column1:

2

Column2:

1

Output:

a	1	true	1	sam  
b	2	false	2	john  

# Author

Siqi Liu<liusq(AT)big(DOT)ac(DOT)cn>
