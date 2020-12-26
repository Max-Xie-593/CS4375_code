from typing import Text, List, Optional
from collections import Counter
from itertools import filterfalse
from math import exp
from numpy import dot

class Data(object):

    def __init__(self, features: List[Text], values: List[List[int]], numiterations: int, alpha: float):
        self.features: List[Text] = features
        self.values: List[List[int]] = values
        self.weights: List[float] = [0.0 for _ in range(len(self.features) - 1)]
        self._neurallearn(numiterations,alpha)

    def __str__(self):
        return str(self.features) + '\n' + '\n'.join(str(x) for x in self.values)

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

    def _sigmoid(self, val: float) -> float:
        return 1 / (1 + exp(-val))

    def _sigmoidP(self, val: float) -> float:
        return self._sigmoid(val) * (1 - self._sigmoid(val))

    def _updateWeights(self, line: List[int], alpha: float) -> None:
        total: float = dot(line,self.weights)
        self.weights = [(x + (alpha * line[idx] * (line[-1] - self._sigmoid(total)) * self._sigmoidP(total))) for idx, x in enumerate(self.weights)]

    def _neurallearn(self, numiteration: int, alpha: float):
        for idx in range(numiteration):
            self._updateWeights(self.values[idx % len(self.values)][:-1],alpha)
            print(
                'After iteration {:d}:\n'.format(idx + 1),
                ', '.join(
                    'w({:s}) = {:0.4f}'.format(feature,x)
                    for feature, x in zip(
                        self.features[:-1],self.weights
                    )
                ),
                'output = {:0.4f}'.format(
                    self._sigmoid(
                        dot(
                            self.values[idx % len(self.values)][:-1],
                            self.weights
                        )
                    )
                )
            )

    def test(self, data: List[List[int]]):
        return len(
            list(
                filter(
                    lambda x: x[0] == x[1],
                    [
                        (
                            round(
                                self._sigmoid(
                                    dot(
                                        x[:-1],
                                        self.weights
                                    )
                                )
                            ),
                            x[-1]
                        )
                        for x in data
                    ]
                )
            )
        ) / len(data)

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
    (x, y) = readFile("train5.txt")
    a: Data = Data(x,y,120,.9)
    (z, test) = readFile('test5.txt')
    print(a.test(test))


if __name__ == "__main__":
    main()
