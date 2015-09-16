Notes about MS-PST documentation 

Errors in documentation :

Error in 3.4 Sample Leaf NBT Page
In this page : https://msdn.microsoft.com/en-us/library/ff387624%28v=office.12%29.aspx , the part "The 4 bytes (03 14 18 01)" is wrong :
- there is no corresponding bytes in the above hexadecimal dump
- it seems that this sentence is a copy of the previous chapter : https://msdn.microsoft.com/en-us/library/ff387868(v=office.12).aspx

2.2.2.7.7.1 BTPAGE
For BBT BTree Type, if cLevel = 0 rgEntries type is BBTENTRY else if cLevel is less than 0, it's BTENTRY.
It seems that cLevel is never less than 0.