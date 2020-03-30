@ECHO OFF
IF NOT EXIST env (
  CALL py -m venv env
  CALL .\env\Scripts\activate
  CALL pip install -r cmdFiles\requirements.txt
) ELSE (
  CALL .\env\Scripts\activate
)
CALL python cmdFiles\BLASTResultsParser.py %1 %2

