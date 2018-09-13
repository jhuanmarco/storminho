from sklearn.datasets import load_svmlight_file
from sklearn import svm, datasets
from sklearn.model_selection import GridSearchCV,RandomizedSearchCV
from sklearn.metrics import f1_score, make_scorer

import arff
import numpy as np
# read arff data
with open("ta.arff") as f:
    # load reads the arff db as a dictionary with
    # the data as a list of lists at key "data"
    dataDictionary = arff.load(f)
    f.close()
# extract data and convert to numpy array
arffData = np.array(dataDictionary['data'])
X = arffData[:, :-1] # data input 
y = arffData[:, -1] # labels


parameters = [
	{
	'kernel':['poly'],
	'degree':[2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,1,50,80,40,30,100],
	#'gamma':[1,0.00001,4,0.21,0.2,0.0125,0.0199,1.999999,1.2,0.019] #0.02
	'C':[0.25]
	}

	]

print('oi')

svc = svm.SVC()
clf = GridSearchCV(svc, parameters)

print('ola')
clf.fit(X, y)
print('tchau')
string = clf.best_params_

for key in string:
	print(key , "=" , string[key])

print(clf.best_score_)