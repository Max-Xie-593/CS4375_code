from typing import Text, List, Optional, Dict, Tuple
from math import inf
from re import split

class Table(object):

    class AdjacencyList(object):

        class Neighbors(object):

            def __init__(self, statereward: List[Text], actions: List[List[Text]]):
                self.state: Text = statereward[0]
                self.reward: int = int(statereward[1])
                self.actions: Dict[Text, Dict[Text,float]] = self._readActions(actions)

            def __str__(self):
                return str(self.state) + ' ' + str(self.reward) + ' ' + str(self.actions)

            def __eq__(self, state):
                if not isinstance(state, Table.AdjacencyList.Neighbors):
                    return NotImplemented
                return self.state == state.state

            @property
            def state(self) -> Text:
                return self._state

            @state.setter
            def state(self, state: Text):
                self._state = state

            @property
            def reward(self) -> int:
                return self._reward

            @reward.setter
            def reward(self, reward: int):
                self._reward = reward

            def _readActions(self, actions: List[List[Text]]) -> Dict[Text, Dict[Text,float]]:
                x: Dict[Text, Dict[Text,float]] = {}
                if actions:
                    for y in actions:
                        if y[0] in x:
                            x[y[0]][y[1]] = float(y[2])
                        else:
                            x[y[0]] = {y[1]: float(y[2])}
                return x

        def __init__(self, data: List[List[List[Text]]]):
            self.list: List[Table.AdjacencyList.Neighbors] = [
                Table.AdjacencyList.Neighbors(
                    x[0],
                    x[1:]
                )
                for x in data
            ]

        def __str__(self) -> Text:
            return '\n'.join(str(x) for x in self.list)

        def __len__(self) -> int:
            return len(self.list)

    class Value(object):

        def __init__(self, action: Optional[Text] = None, reward: float = -inf):
            self.action: Optional[Text] = action
            self.reward: float = reward

        def __str__(self) -> Text:
            return str(self.action) + " " + '{:0.4f}'.format(self.reward)

        @property
        def action(self) -> Optional[Text]:
            return self._action

        @action.setter
        def action(self, action: Text):
            self._action = action

        @property
        def reward(self) -> float:
            return self._reward

        @reward.setter
        def reward(self, reward: float):
            self._reward = reward

    def __init__(self, graph: List[List[List[Text]]], gamma: float):
        self.graph: Table.AdjacencyList = Table.AdjacencyList(graph)
        self.gamma: float = gamma
        self.table: List[List[Table.Value]] = [[Table.Value() for _ in range(len(self.graph))] for _ in range(20)]
        self._valueIterate()

    def __str__(self) -> Text:
        return '\n'.join('After iteration {:d}: '.format(idx + 1) + ' '.join(map(str,x)) for idx, x in enumerate(self.table))

    def _valueIterate(self) -> None:
        self.table[0] = [Table.Value(list(self.graph.list[idx].actions.keys())[0], self.graph.list[idx].reward) for idx, x in enumerate(self.table[0])]
        for r, row in enumerate(self.table[1:]):
            for c, val in enumerate(row):
                possibleVals: List[float] = [
                    sum(
                        self.table[r][
                            self.graph.list.index(
                                Table.AdjacencyList.Neighbors(
                                    [newState,'0'],
                                    []
                                )
                            )
                        ]
                        .reward * chance
                        for newState, chance in destinations.items()
                    )
                    for action, destinations in self.graph.list[c].actions.items()
                ]
                val.reward = (max(possibleVals) * self.gamma) + self.graph.list[c].reward
                val.action = list(self.graph.list[c].actions.keys())[possibleVals.index(max(possibleVals))]


def readFile(file: Text) -> List[List[List[Text]]]:
    with open(file) as f:
        return [
            [
                data.split()
                for data in list(
                    filter(
                        None,
                        split(
                            r'\)?\s\(|\)',
                            line.strip()
                        )
                    )
                )
            ]
            for line in f.readlines()
            if line.strip()
        ]

def main():
    data: List[List[Text]] = readFile("test22.txt")
    adList: Table = Table(data,.6)
    print(adList)

if __name__ == "__main__":
    main()
