# Usage

It translates the lines in a file. Input and output formats are CSV (separated by '\t'). The expression can contain several fields separated by empty space(s). Each field is an expression containing constant strings and/or variables. Available variables are $0, $1, $2, etc. $0 represents the current line of input file. $1 ... $n are the first to n-th fields of the line. It also supports basic arithmetic and comparison operators.

#Example

Input:

1	John	19  
2	Richard	23  
3	Anna	20  

Expression:

"Name:"$2 $3+$1  

Output:

Name:John	20  
Name:Richard	25  
Name:Anna	23  

# Author

Siqi Liu<liusq(AT)big(DOT)ac(DOT)cn>
