PhysXワールドサンプル
========

<img src="http://web-prog.com/wp-content/uploads/physx-world.png">

<a href="http://web-prog.com/">OpenGL ESからJBulletまで3DAndroid入門@プロバナ</a>で作ったプログラムを置きます。

2013/9/20に大幅アップデートをしました。

今回から3Dワールド内を自由に歩き回ることができる、FPS風の仕様になっています。

また、非効率的なプログラムを諸所、書き直しました。

～ワールド内でできるイベント一覧～
========
    /* タップ位置へへキューブ弾を発射（画面のシングルタップ） */
    if(diffTime < 100)
	    myRenderer.shootCube(event);

<img src="http://web-prog.com/wp-content/uploads/tap-cube.png">
    
    /* 昼夜の切り替え（メニューより選択） */
    mWorld.darkSwitch();
    
<img src="http://web-prog.com/wp-content/uploads/dark-nondark.png">
    
    /* キューブ雨を降らせる（メニューより選択） */
    mWorld.rainSwitch();
    
<img src="http://web-prog.com/wp-content/uploads/falling-cube.png">
    
    /* 前後左右へ平行移動（前・左・右・後ボタン） */
    myRenderer.setForward(true);
    myRenderer.setLeft(true);
    myRenderer.setRight(true);
    myRenderer.setBack(true);

<img src="http://web-prog.com/wp-content/uploads/forward.png">
    
    /* 視点を回転する（レンダラ内をスクロール） */
    myRenderer.lookRotation((event.getX() - distanceX) / 10);
    
<img src="http://web-prog.com/wp-content/uploads/look-rotate.png">

神々しいnullieさんの物理演算サンプルである、HelloGameを大変参考にさせていただきました。

本当に感謝です。

<a href="https://github.com/nullie/HelloGame">nullie/HelloGame</a>
