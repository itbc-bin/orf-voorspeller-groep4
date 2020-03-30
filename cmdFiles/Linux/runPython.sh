#! /bin/bash
if [ ! -d env ]; then
  python3 -m venv env
  source cmdFiles/Linux/activate.sh
  pip3 install -r cmdFiles/requirements.txt
else
  source cmdFiles/Linux/activate.sh
fi
python3 cmdFiles/BLASTResultsParser.py $1 $2

