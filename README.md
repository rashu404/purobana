PhysXワールドサンプル
========

<img src="http://web-prog.com/wp-content/uploads/purobanaWorld2-450x318.png">

<a href="http://web-prog.com/">OpenGL ESからJBulletまで3DAndroid入門@プロバナ</a>で作ったプログラムを置きます。

タッチによる挙動は、WorldActivityクラス内、onTouchEvent()にて変化します。

～タッチによって発生するイベント一覧～
========
    mRenderer.shootInit();
    /* 固定位置へキューブ弾を発射 */
    			
    mRenderer.darkSwitch();
    /* 昼夜の切り替え */
    			
    mRenderer.translateX(true);
    /* 電車風景のようにカメラの平行移動（TOUCH_UPでfalseにすること） */
    			
    Vector3f point = mRenderer.getRayTo((int)ev.getX(), (int)ev.getY());
    mRenderer.shootInit(point);
    /* タッチ位置へキューブ弾を発射 */
    
    mRenderer.fallingSwitch(b);
    b = !b;
    /* タッチでランダム地点にキューブ雨を降らせる */
          

神々しいnullieさんの物理演算サンプルである、HelloGameを大変参考にさせていただきました。
本当に感謝です。

<a href="https://github.com/nullie/HelloGame">nullie/HelloGame</a>
