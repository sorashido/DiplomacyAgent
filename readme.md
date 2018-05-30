# diplomacy agent
diplomacy agent
http://staff.scem.westernsydney.edu.au/~dave/bandana/

## simulation
- https://github.com/sorashido/DiplomacyTournament

## Link
- https://dl.acm.org/citation.cfm?id=1838510
- https://github.com/AngelaFabregues/dipGame
- http://www.iiia.csic.es/~davedejonge/bandana/files/Bandana%201.3%20Manual.pdf

## 戦略
### 交渉
#### 評価値作成
補給地の数から優勢，劣勢の状態(128通り)を決める，
状態，年によって自分のlocationをカウント

自分が優勢になるような評価値が高くなる
劣勢になるようなものは評価値が低い

#### 敵のモデル化
プレイデータから年毎の補給地数を調べ事前に相性を取る
交渉の内容によって相手との敵対度を作る

#### 交渉の提案
敵対度が高い相手が不利になるように提案していく

#### 交渉の受け入れ
自分にとって評価値が高ければよし
