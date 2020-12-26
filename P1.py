from typing import Text, List, Optional
from collections import Counter
from itertools import filterfalse
from math import log2
from copy import deepcopy

class Tree(object):

    class Node(object):

        class Data(object):

            def __init__(self, features: List[Text], values: List[List[int]]):
                self.features: List[Text] = features
                self.values: List[List[int]] = values

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

        def __init__(self, features: List[Text], values: List[List[int]]):
            self.left: Tree.Node = None
            self.right: Tree.Node = None
            self.data: Tree.Node.Data = Tree.Node.Data(features,values)
            self.entropy: float = self._calculateEntropy(self.data.values)
            self.featureSplit: Optional[Text] = None
            self.classVal: int = None

        def __str__(self):
            return str(self.classVal)

        @property
        def left(self):
            return self._left

        @left.setter
        def left(self, left):
            self._left = left

        @property
        def right(self):
            return self._right

        @right.setter
        def right(self, right):
            self._right = right

        @property
        def featureSplit(self):
            return self._featureSplit

        @featureSplit.setter
        def featureSplit(self, featureSplit):
            self._featureSplit = featureSplit

        @property
        def classVal(self):
            return self._classVal

        @classVal.setter
        def classVal(self, classVal):
            self._classVal = classVal

        def isLeaf(self) -> bool:
            return self.left is None and self.right is None

        def determineBestSplit(self) -> Optional[Text]:
            copyData: Tree.Node.Data = deepcopy(self.data)
            bestSplit: Optional[Text] = None
            informationGain: float = -0.1
            for idx,feature in enumerate(copyData.features[:-1]):
                zeroData: List[List[int]] = list(filter(lambda x: x[idx] == 0,copyData.values))
                oneData: List[List[int]] = list(filterfalse(lambda x: x[idx] == 0,copyData.values))
                fraction0: float = len(zeroData) / len(copyData.values) if len(copyData.values) > 0 else 0
                condEntropy: float = (fraction0 * self._calculateEntropy(zeroData)) + ((1 - fraction0) * self._calculateEntropy(oneData))
                if self.entropy - condEntropy > informationGain:
                    bestSplit = feature
                    informationGain = self.entropy - condEntropy
            return bestSplit

        def _calculateEntropy(self, data: List[List[int]]) -> float:

            def nlog2(val: float) -> float:
                return log2(val) if val != 0 else 0

            count: Counter = Counter([x[-1] for x in data])
            zeroFraction: float = count[0] / len(data) if len(data) > 0 else 0
            return abs(
                -(
                    (
                        zeroFraction * nlog2(zeroFraction)
                    ) + (
                        (1 - zeroFraction) * nlog2(1 - zeroFraction)
                    )
                )
            )

        def isDone(self) -> bool:
            if len(self.data.features) == 1 or self.determineBestSplit() is None:
                return True
            count: Counter = Counter([x[-1] for x in self.data.values])
            return count[0] == 0 or count[1] == 0

    def __init__(self, features: List[Text], values: List[List[int]]):
        self.root: Tree.Node = Tree.Node(features,values)
        self.mostCommon: int = self._determineMostCommonClass()

    def __repr__(self):
        return 'tree representation'

    def __str__(self):
        return self._treeStr(self.root, 0)

    def _treeStr(self,node: Node, depth: int) -> Text:
        string: Text = ''
        if node.isLeaf():
            return string
        depth += 1
        string += '| ' * depth + str(node.featureSplit) + ' = 0 :'
        string += ' ' + str(node.left.classVal) + '\n' if node.left.isDone() else '\n'
        string += self._treeStr(node.left, depth)
        string += '| ' * depth + str(node.featureSplit) + ' = 1 :'
        string += ' ' + str(node.right.classVal) + '\n' if node.right.isDone() else '\n'
        string += self._treeStr(node.right, depth)
        return string

    def _determineMostCommonClass(self) -> int:
        count: Counter = Counter([x[-1] for x in self.root.data.values])
        return 0 if count[0] > count[1] else 1

    def _split(self, node: Node) -> None:
        if node.determineBestSplit() is None:
            return
        copyData: Tree.Node.Data = deepcopy(node.data)
        feature: Optional[Text] = node.determineBestSplit()
        idx: int = copyData.features.index(feature)
        node.left = Tree.Node(
            copyData.features[:idx] + copyData.features[idx + 1:],
            [
                x[:idx] + x[idx + 1:]
                for x in list(
                    filter(
                        lambda x: x[idx] == 0,copyData.values
                    )
                )
            ]
        )
        node.right = Tree.Node(
            copyData.features[:idx] + copyData.features[idx + 1:],
            [
                x[:idx] + x[idx + 1:]
                for x in list(
                    filterfalse(
                        lambda x: x[idx] == 0,copyData.values
                    )
                )
            ]
        )
        node.featureSplit = feature

    def learn(self) -> None:
        self._learn(self.root)

    def _learn(self, node: Node) -> None:
        if node is None:
            return
        if node.isDone():
            self._setClassValue(node)
            return
        self._split(node)
        self._learn(node.left)
        self._learn(node.right)

    def _setClassValue(self, node: Node) -> None:
        count: Counter = Counter([x[-1] for x in node.data.values])
        node.classVal = self.mostCommon if count[0] == count[1] else 0 if count[0] > count[1] else 1

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
    a: Tree = Tree(x,y)
    a.learn()
    print(a)


if __name__ == "__main__":
    main()
