from typing import Text, List, Optional, Tuple
from collections import Counter
from itertools import filterfalse
from math import log2
from copy import deepcopy

class Data(object):

    def __init__(self, features: List[Text], values: List[List[int]]):
        self.features: List[Text] = features
        self.values: List[List[int]] = values
        (self.zero, self.one) = self._calculateprob()

    def __str__(self):
        return str(self.features) + '\n' + '\n'.join(
            str(x) for x in self.values
        ) + '\n\n' + '\n'.join(
            [
                self._strProbs(
                    self.zero,
                    0
                ),
                self._strProbs(
                    self.one,
                    1
                )
            ]
        )

    def _strProbs(self, vals: List[float], classVal: int) -> Text:
        return 'P(class = {:d}) = {:0.2f} '.format(
            classVal,
            vals[0]
        ) + '\n\t' + '\n\t'.join(
            [
                'P({:s} = 0|{:d}) = {:0.2f} P({:s} = 1|{:d}) = {:0.2f}'.format(
                    feat,
                    classVal,
                    vals[(idx * 2) + 1],
                    feat,
                    classVal,
                    vals[(idx * 2) + 2]
                )
                for idx, feat in enumerate(
                    self.features[:-1]
                )
            ]
        )

    def __len__(self):
        return len(self.values)

    @property
    def features(self):
        return self._features

    @features.setter
    def features(self, features: List[Text]):
        self._features = features

    @property
    def values(self):
        return self._values

    @values.setter
    def values(self, values: List[List[int]]):
        self._values = values

    def _calculateprob(self):
        copyData: Data = deepcopy(self.values)
        return (
            self._calculatecondprob(
                list(
                    filter(
                        lambda x: x[-1] == 0,
                        copyData
                    )
                )
            ),
            self._calculatecondprob(
                list(
                    filterfalse(
                        lambda x: x[-1] == 0,
                        copyData
                    )
                )
            )
        )

    def _calculatecondprob(self, data: List[List[int]]) -> List[float]:
        probVals: List[float] = [0.0] * ((len(self.features) * 2) - 1)
        if not data:
            return probVals
        probVals[0] = len(data) / len(self.values)
        for idx,feature in enumerate(self.features[:-1]):
            count: Counter = Counter([x[idx] for x in data])
            probVals[(idx * 2) + 1] = count[0] / len(data)
            probVals[(idx * 2) + 2] = count[1] / len(data)
        return probVals

    def test(self, data: List[List[int]]):
        return len(
            list(
                filter(
                    lambda x: x[0] == x[1],
                    [
                        (
                            self.testline(x),
                            x[-1]
                        )
                        for x in data
                    ]
                )
            )
        ) / len(data)

    def testline(self, line: List[int]):
        isZero: float = self.zero[0]
        isOne: float = self.one[0]
        for idx,val in enumerate(line[:-1]):
            if val == 0:
                isZero *= self.zero[(idx * 2) + 1]
                isOne *= self.one[(idx * 2) + 1]
            else:
                isZero *= self.zero[(idx * 2) + 2]
                isOne *= self.one[(idx * 2) + 2]
        return 0 if isZero > isOne else 1


def readFile(file: Text):
    with open(file) as f:
        lines: List[Text] = [
            line.strip()
            for line in f.readlines()
            if line.strip()
        ]
    return (
        lines[0].split(),
        [
            [
                int(x)
                for x in y.split()
            ]
            for y in lines[1:]
        ]
    )

def main():
    (x, y) = readFile("train2.txt")
    a: Data = Data(x,y)
    print(a)
    (b, testD) = readFile('test2.txt')
    print('{:0.2f}'.format(a.test(testD)))

if __name__ == "__main__":
    main()
