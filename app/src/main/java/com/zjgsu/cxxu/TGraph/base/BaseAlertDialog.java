package com.zjgsu.cxxu.TGraph.base;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
//import androidx.annotation.IdRes;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.StyleRes;
//import androidx.appcompat.app.AlertDialog;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.zjgsu.cxxu.TGraph.Lg;

import org.jetbrains.annotations.Nullable;


public abstract class BaseAlertDialog extends AlertDialog {
    private static final String TAG = "BaseAlertDialog";
    private View mainView;

    protected BaseAlertDialog(@NonNull Context context) {
        this(context, 0);
    }

    protected BaseAlertDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    /**
     * AlertDialog Set the view to display in the dialog. This method has no effect if called after show().
     * Overrides:
     * setView in class AlertDialog
     *
     * @param view
     */
    @Override
    public void setView(View view) {
        Lg.d(TAG, "you invoke the setView ,and the ItemView:" + view + "will be assign to the mainView.");
        super.setView(view);
        mainView = view;
        onBaseBindView();
    }

    /**
     * 此处重写了findViewById方法,在您将要改该视图结点的时候(弹出结点编辑对话框的时候),本方法会根据需要调用,用于绑定结点微件
     * 在本app设计中结点编辑有两种情况:
     * select某个结点然后为该节点添加一个子节点/兄弟结点(不可以对根节点操作)
     * 或者,您可以通过长按某个结点,这样,该节点就会提供给你编辑该节点的窗口.
     * <p>override from:
     * findViewById in class AppCompatDialog
     */
    @Nullable
    @Override
    public View findViewById(@IdRes int id) {
        /**/
        Lg.d(TAG, "you are running the override_ed findViewById()");
        Lg.d(TAG, "" + mainView);
        if (mainView != null) {
            /**
             * android.view.ItemView
             * public final <T extends ItemView> T findViewById(@IdRes int id)
             * Finds the first descendant(后裔) view with the given ID,
             * the view itself if the ID matches getId(), or null if the ID is invalid (< 0) or there is no matching view in the hierarchy.
             * 查找具有给定ID的第一个后代视图，如果ID匹配getId()，则查找视图本身，如果ID无效(< 0)或在层次结构中没有匹配的视图，则为null。
             * 注意:在大多数情况下——取决于编译器的支持——生成的视图会自动转换为目标类类型。
             * 如果目标类类型是无约束的，则可能需要显式强制转换*/
            View resultView = mainView.findViewById(id);
            Lg.d(TAG, resultView + "was found from the mainView.");
            return resultView;
        }
        return super.findViewById(id);
    }

    protected abstract void onBaseBindView();

}
