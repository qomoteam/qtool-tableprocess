package cn.ac.big.cbb.biocloud.tablemanip.expression;
import static cn.ac.big.cbb.biocloud.tablemanip.expression.Token.Type.*;

/** This is the lexical analyzer for boolean and arithmic expressions
 *
 */
%%

%class LexicalAnalyzer
%unicode
%function nextToken
%type Token

%{
StringBuffer string = new StringBuffer();
private Token newToken(Token.Type type){
return new Token(type,yytext());
}
%}

%eofval{
return new Token(EOF,null);
%eofval}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

Identifier = ([:jletter:]|\$)[:jletterdigit:]*

DecNumLiteral = [0-9]+(\.[0-9]+)?

%state STRING

%%

<YYINITIAL> {
  /* identifiers */ 
  {Identifier}                   { return newToken(ID); }
 
  /* literals */
  {DecNumLiteral}            { return newToken(NUM); }
  \"                             { string.setLength(0); yybegin(STRING); }

  /* operators */
  "=~"                           { return newToken(MATCH); }
  "=="                           { return newToken(COMP); }
  "!="                           { return newToken(COMP); }
  "<="                           { return newToken(COMP); }
  ">="                           { return newToken(COMP); }
  "<"                           { return newToken(COMP); }
  ">"                           { return newToken(COMP); }
  "+"                            { return newToken(ARITH); }
  "-"                            { return newToken(ARITH); }
  "*"                            { return newToken(ARITH); }
  "/"                            { return newToken(ARITH); }
  "^"                            { return newToken(ARITH); }
  "||"                            { return newToken(LOG); }
  "&&"                            { return newToken(LOG); }
  "!"                            { return newToken(UNI); }
  "("                            { return newToken(LRB); }
  ")"                            { return newToken(RRB); }
  ","                            { return newToken(COMMA); }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
}

<STRING> {
  \"                             { yybegin(YYINITIAL); 
                                   return new Token(STR,string.toString()); }
  [^\n\r\"\\]+                   { string.append( yytext() ); }
  \\t                            { string.append('\t'); }
  \\n                            { string.append('\n'); }

  \\r                            { string.append('\r'); }
  \\\"                           { string.append('\"'); }
  \\                             { string.append('\\'); }
}

/* error fallback */
.|\n                             { throw new UnexpectedCharacterException(yytext()); }
