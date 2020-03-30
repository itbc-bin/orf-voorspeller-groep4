IF NOT EXIST env(
  py -m venv env
  source pythonFiles/Linux/activate.sh
  pip install -r pythonFiles/requirements.txt
)
python pythonFiles/BLASTResultsParser.py %1 %2

