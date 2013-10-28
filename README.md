PhysXワールドサンプル
========

<img src="http://web-prog.com/wp-content/uploads/start_ana.png">

<a href="http://web-prog.com/">OpenGL ESからJBulletまで3DAndroid入門@プロバナ</a>で作ったプログラムを置きます。

2013年10月28日にアナログスティックUIを追加し、四方八方の移動が可能になりました。

今回の主な更新点は以下です。

・空と地面のリアリティを改善

・諸所のコードを読みやすく

3Dワールド内を自由に歩き回ることができる、FPS風の仕様になっています。

～ワールド内でできるイベント一覧～
========
    /* タップ位置へへキューブ弾を発射（画面のシングルタップ） */
    if(diffTime < 100)
	    myRenderer.shootCube(event);

<img src="http://web-prog.com/wp-content/uploads/shoot_ana.png">
    
    /* 昼夜の切り替え（メニューより選択） */
    mWorld.darkSwitch();
    
<img src="http://web-prog.com/wp-content/uploads/dark_analog.png">
    
    /* キューブ雨を降らせる（メニューより選択） */
    mWorld.rainSwitch();
    
<img src="http://web-prog.com/wp-content/uploads/falling_ana.png">

    /* 指定した角度へ移動する */
    MyRenderer myRenderer = PhysxWorldActivity.getMyRenderer();
    myRenderer.setMove(onAnalogStick);
		
    myRenderer.setMoveAngle((int)Math.toDegrees(moveAng));


    /* 視点を回転する（レンダラ内をスクロール） */
    myRenderer.lookRotation((event.getX() - distanceX) / 10);
    
神々しいnullieさんの物理演算サンプルである、HelloGameを大変参考にさせていただきました。

本当に感謝です。

<a href="https://github.com/nullie/HelloGame">nullie/HelloGame</a>
