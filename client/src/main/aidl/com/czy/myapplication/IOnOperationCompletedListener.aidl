// IOnOperationCompletedListener.aidl
package com.czy.myapplication;

// Declare any non-default types here with import statements
import com.czy.myapplication.Parameter;
interface IOnOperationCompletedListener {
    void onOperationCompleted(in Parameter result);
}
