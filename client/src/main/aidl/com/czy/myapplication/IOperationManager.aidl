// IOperationManager.aidl
package com.czy.myapplication;

// Declare any non-default types here with import statements
import com.czy.myapplication.Parameter;
import com.czy.myapplication.IOnOperationCompletedListener;
interface IOperationManager {
    void operation(in Parameter parameter1 , in Parameter parameter2);

     void registerListener(in IOnOperationCompletedListener listener);

     void unregisterListener(in IOnOperationCompletedListener listener);
}
