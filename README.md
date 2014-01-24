PhysXワールドサンプル
========

<img src="http://web-prog.com/wp-content/uploads/fieldMap.png">

<a href="http://web-prog.com/">OpenGL ESからJBulletまで3DAndroid入門@プロバナ</a>で作ったプログラムを置きます。

2014年01月24日に3D空間にあるカメラ位置付近のオブジェクトをマップにしました。

以下が今回のアップデート内容です。

・マップの追加

・OpenGL ESの不要なメソッドを削除

・ソースコードの整理

3Dワールド内を自由に歩き回ることができる、FPS風の仕様になっています。

～ワールド内でできるイベント一覧～
========
    /* タップ位置へへキューブ弾を発射（画面のシングルタップ） */
    if(diffTime < 100)
	    myRenderer.shootCube(event);

<img src="http://web-prog.com/wp-content/uploads/shoot_ana.png">
    
    /* 昼夜の切り替え */
    mWorld.darkSwitch();
    
<img src="http://web-prog.com/wp-content/uploads/dark_analog.png">
    
    /* キューブ雨を降らせる（現在は削除） */
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
