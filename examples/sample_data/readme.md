# Data

## BTC
For BTC, place `tx_inputs_100K.csv` or similar file here.
Then run `cat tx_inputs_100K.csv | python preprocess.py > btc_inputs.csv` to generate CSV file with unique, comma separated integers instead of hashes.
One line per transaction, i.e. each line is one union operation in the clustering algorithm.

## XMR
Place csv file with header `inid, outid` and matching data here. The first are input IDs, the second output IDs referenced in inputs with the given input ID.
E.g. a TX that references outputs 1-5 in input 1 would look like:
```
inid,outid # if header is defined, please set corresponding option in data reader
1,1
1,2
1,3
1,4
1,5
```
If hashes are provided instead, do it like `x'InHash1','OutHash2'` where x could be anything (e.g. `cat -n FileWithout_x.csv > FileWith_x.csv`). The reason is, that `preprocess.py` splits at `'` and then uses all elements with odd index.

`cat -n hashes_without_x.csv | python preprocess.py > valid_inid_outid_file.csv`
`cat hashes_with_x.csv | python preprocess.py > valid_inid_outid_file.csv`