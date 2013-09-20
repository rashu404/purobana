PhysXワールドサンプル
========

<img src="http://web-prog.com/wp-content/uploads/purobanaWorld2-450x318.png">

<a href="http://web-prog.com/">OpenGL ESからJBulletまで3DAndroid入門@プロバナ</a>で作ったプログラムを置きます。

タッチによる挙動は、MyRendererクラス内、onTouchEvent()にて変化します。

2013/7/10にパッケージとクラスを整理したので、全ての動作確認はできていません。
また、デフォルトでカメラが回転します。

～タッチによって発生するイベント一覧～
========
    mWorld.shootInit();
    /* 固定位置へキューブ弾を発射 */
    
<img src="http://web-prog.com/wp-content/uploads/dark1.png">
    			
    mWorld.darkSwitch();
    /* 昼夜の切り替え */

    mWorld.translateX(true);
    /* 電車風景のようにカメラの平行移動（TOUCH_UPでfalseにすること） */
    			
<img src="http://web-prog.com/wp-content/uploads/touchShootTop.png">    
                
    Vector3f point = PhysicsUtils.getRayTo((int)event.getX(), (int)event.getY(), eye, look, up, width, height);
    mWorld.shootInit(point);
    /* タッチ位置へキューブ弾を発射 */
    
<img src="http://web-prog.com/wp-content/uploads/fallingCubeTop-450x252.png">
    
    mWorld.fallingSwitch(b);
    b = !b;
    /* タッチでランダム地点にキューブ雨を降らせる */
          

神々しいnullieさんの物理演算サンプルである、HelloGameを大変参考にさせていただきました。
本当に感謝です。

<a href="https://github.com/nullie/HelloGame">nullie/HelloGame</a>
