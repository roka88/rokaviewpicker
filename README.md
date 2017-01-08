# RokaViewPicker
안드로이드 Vertical ViewPicker

기존 NumberPicker에서는 각 픽커마다 색깔을 바꾸거나, 자료형을 집어넣는 형태라 커스텀하기 힘들어서
직접 Vertical한 Picker를 만듬


#Gradle

<pre>
repositories {
    maven { url "https://jitpack.io" }
}
</pre>

<pre>
dependencies {
    compile 'com.github.roka88:rokaviewpicker:0.0.1'
}
</pre>

#Version
<pre>
0.0.1 초기버전
</pre>

#메서드
<pre>

RokaViewPicker<TextView> mRootView;

mRootView.addPickerView(view); // 뷰를 픽커에 넣는다
mRootView.addPickerView(view, 1) // 해당 위치에 뷰를 픽커에 넣는다.
mRootView.removePickerAllView() // 픽커내부의 뷰를 지운다.
mRootView.removePickerView(1) // 해당 위치의 뷰를 지운다.
mRootView.removePickerView(view) // 해당 뷰를 지운다.
mRootView.setSelectPicker(3) // 해당 위치로 피커를 변경한다.
mRootView.getCurrentPicker() // 현재 Pick된 index를 구한다.

</pre>



#주의사항
<pre>
1. ImageView를 인플레이트 시, 뷰를 Add할 때마다 직접 param을 지정해줘야함.(아니면 이미지크기로 변경)
2. setSelectPicker(int position) 을 사용할 경우, 뷰 생성이 완료된 후에야 호출해야함 그전에 호출 시 에러

방법은 ViewObserverTree를 이용하여 변경

private ViewTreeObserver.OnGlobalLayoutListener pickerObserver = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            mRootView.setSelectPicker(3);
        }
};

mRootView.getViewTreeObserver().addOnGlobalLayoutListener(pickerObserver);


</pre>


#Interface
<pre>
public interface OnPickerScrollChangeListener {
    void onPickerScrollChangeListener(int x, int y);
}

public interface OnPickerScrollEndListener {
    void onPickerScrollEndListener(View v, int position);
}

public interface OnPickerScrollStartListener {
    void onPickerScrollStartListener(View v, int position);
}

</pre>


#License
<pre>
Copyright 2017 Roka

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
