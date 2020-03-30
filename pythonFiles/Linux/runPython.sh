#! /bin/bash
if [ ! -d env ]; then
  python3 -m venv env
  source pythonFiles/Linux/activate.sh
  pip3 install -r pythonFiles/requirements.txt
fi
python3 pythonFiles/BLASTResultsParser.py $1 $2

