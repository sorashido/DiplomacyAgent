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
状態，年による人間プレイヤーのlocationが出てくる

今の次のターンのlocationが評価値が高いようにする．
状態は取り敢えず前の年と同じものを用いる．

交渉は自分，相手ともに重要な場所
自分にとって重要，相手にとって重要でない
相手にとって重要，自分にとって重要でない
どちらにとっても重要でない

と分けられる

#### 敵のモデル化
プレイデータから年毎の補給地数を調べ事前に相性を取る
交渉の内容によって相手との敵対度を作る

自分にとって評価値が高い提案なら，敵対度は低くなる．
自分にとって評価値が低い提案なら，敵対度は高くなる．

#### 交渉の提案
敵対度が高い相手に対しては自分，相手にともに重要な場所を提案．

敵対度が低ければ，相手にとって重要な土地，自分にとって重要でない場所に加えて，
自分にとって重要，相手にとって重要でない場所を提案．

#### 交渉の受け入れ
自分にとって評価値が高ければよし
