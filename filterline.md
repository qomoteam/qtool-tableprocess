# Usage

It filters the lines in a CSV file. Input and output formats are CSV (separated by TAB). The expression is a boolean expression evaluated to true or false. If a line satisfies the expression, it is printed to the output file; otherwise, not.  The expression can contains constant strings and/or variables. Available variables are $0, $1, $2 ... $0 represents the current line of input file. $1 ... $n is the first to nth field of the line. It also supports basic arithmetic and comparision operators.

## Operators

Comparison operators:  

* =~ : regex match  
* ==  
* !=  
* \>  
* \>=  
* <  
* <=  

Arithmetic operators:  

* \+  
* \-  
* \*  
* /  
* ^ : exponential  
* ln(): natural logarithm  

Logical operators:  

* \! : not  
* && : and  
* || : or  

# Example

Input:  

1	John	19  
2	Richard	23  
3	Anna	20  

Expression:

$3>20  

Output:

2	Richard	23  

# Author

Siqi Liu<liusq(AT)big(DOT)ac(DOT)cn>
