# ClassificationOfStyle

垂直的Tablayout 阻尼翻页效果

# 加载
Step 1. Add the JitPack repository to your build file

    allprojects {
      repositories {
        ...
        maven { url 'https://jitpack.io' }
      }
    }
    
Step 2. Add the dependency

    dependencies {
            implementation 'com.github.xuxingjia:ClassificationOfStyle:v1.0.0'
    }

# How to Use

Step 1: In XML


     <com.example.xuxingjia.classificationofstyle.view.VerticalTabLayout
            android:id="@+id/vtl_vertical_tablayout"
            android:layout_width="67dp"
            android:layout_height="match_parent" />
                          
    <com.example.xuxingjia.classificationofstyle.view.CustomDampView
            android:id="@+id/cdv_damp_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/> 
            
Step 2: Init and setListener

     //VerticalTabLayout选中监听
     mVerticalTablayout = (VerticalTabLayout) findViewById(R.id.vtl_vertical_tablayout);
            mVerticalTablayout.setLeftSelectListener(new VerticalTabLayout.LeftSelectListener() {
                @Override
                public void leftSelectListener(int i) {

                }
            });
            
        //CustomDampView右侧选中滑动监听
        mDampView = (CustomDampView) findViewById(R.id.cdv_damp_view);
        mDampView.setOnReightSelectListener(new CustomDampView.OnReightSelectListener() {
            @Override
            public void onReightSelectListener(int i) {
                
            }
        });
        
        
# More

设置VerticalTabLayout:

    //设置VerticalTabLayout 数据集合
    mVerticalTablayout.setData(List<String> data);
    
    //选中指定索引效果
    mVerticalTablayout.setLeftSelect(int position);
    
设置CustomDampView:

    //添加子控件集合
    mDampView.setChildNumber(List<CustomChildView> customChildViews);
    //在子控件中添加自己想要添加的控件内容
    
    
     CustomChildView childView = new CustomChildView(getActivity());
     childView.initChildView(View view);
