package wang.raye.preioc;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import wang.raye.preioc.find.AbstractFind;
import wang.raye.preioc.find.ActivityFind;
import wang.raye.preioc.find.DialogFind;
import wang.raye.preioc.find.ViewFind;
/**
 * ��������ע�����
 * ����ͨע���ܲ�ͬ���Ǵ�ע���ǵ���Ԥ����õĴ���ʵ��ע�룬
 * �������ṩ���޽ӽ�ԭ��������
 * @author Raye
 *
 */
public class PreIOC {

	private static final boolean debug = true;
	private static final String TAG = PreIOC.class.getName();
	private static final ViewBinder<Object> NULL_BIND = new ViewBinder<Object>() {
		
		@Override
		public void binder(AbstractFind finder, Object target, Object source) {
			
		}
	};
	/** ����*/
	private static final Map<Class<?>, ViewBinder<Object>> BINDERS = new LinkedHashMap<>();
	private static final Map<Class<?>, ViewDataBinder> DATABINDERS = new LinkedHashMap<>();

	public static void binder(Activity activity) {
		binder(activity,activity,new ActivityFind());
	}
	
	public static void binder(View view){
		binder(view, view, new ViewFind());
	}
	
	public static void binder(Dialog dialog){
		binder(dialog,dialog,new DialogFind());
	}
	
	/**
	 * ��ָ������Ķ��������
	 * @param target ���󶨵����Ե�������Ķ���
	 * @param activity �ؼ����ڵ�Activity
	 */
	public static void binder(Object target,Activity activity){
		binder(target,activity,new ActivityFind());
	}
	
	/**
	 * ��ָ������������
	 * @param target ���󶨵����Ե�������Ķ���
	 * @param dialog �ؼ�����Dialog
	 */
	public static void binder(Object target,Dialog dialog){
		binder(target,dialog,new DialogFind());
	}
	
	/**
	 * ������
	 * @param viewHolder
	 */
	public static void binderData(Object viewHolder,BaseAdapter adapter,int position){
		Class<?> targetClass = viewHolder.getClass();
		try {
			ViewDataBinder vdb = findVDBForClass(targetClass);
			if(vdb != null){
				vdb.bindData(viewHolder, adapter, position);
			}
		}catch(Exception e){
			throw new RuntimeException("Unable to bind data for " + targetClass.getName(), e);
		}
	}
	/**
	 * ��ָ������������
	 * @param target ���󶨵����Ե�������Ķ���
	 * @param dialog �ؼ�����View
	 */
	public static void binder(Object target,View view){
		binder(target,view,new ViewFind());
	}

	private static void binder(Object target, Object source, AbstractFind finder) {
		Class<?> targetClass = target.getClass();
		try {
			ViewBinder<Object> viewBinder = findViewBinderForClass(targetClass);
			if (viewBinder != null) {
				Log.i(TAG, "ʵ�����ɹ�����ʼ��:"+viewBinder.getClass().getName());
				viewBinder.binder(finder, target, source);
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to bind views for " + targetClass.getName(), e);
		}
	}

	private static ViewBinder<Object> findViewBinderForClass(Class<?> cls)
			throws IllegalAccessException, InstantiationException {
		ViewBinder<Object> viewBinder = viewBinder = BINDERS.get(cls);
		if (viewBinder != null) {
			if (debug)
				Log.d(TAG, "ProIOC: �ӻ����л�ȡ��.");
			return viewBinder;
		}
		
		String clsName = cls.getName();
		if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
			if (debug)
				Log.w(TAG, "ProIOC: ��ǰ�಻֧��ע��");
			return NULL_BIND;
		}
		try {
			Class<?> viewBindingClass = Class.forName(clsName + "$$ViewBinder");
			viewBinder = (ViewBinder<Object>) viewBindingClass.newInstance();
			if (debug)
				Log.d(TAG, "ProIOC: �ɹ�����ViewBinder.");
		} catch (ClassNotFoundException e) {
			if (debug)
				Log.w(TAG, "ʵ����ʧ�ܣ�������Ѱ " + cls.getSuperclass().getName());
			viewBinder = findViewBinderForClass(cls.getSuperclass());
		}
		BINDERS.put(cls, viewBinder);
		return viewBinder;
	}
	
	private static ViewDataBinder findVDBForClass(Class<?> cls)
			throws IllegalAccessException, InstantiationException {
		ViewDataBinder viewBinder = DATABINDERS.get(cls);
		if (viewBinder != null) {
			if (debug)
				Log.d(TAG, "ProIOC: �ӻ����л�ȡ��.");
			return viewBinder;
		}
		
		String clsName = cls.getName();
		if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
			if (debug)
				Log.w(TAG, "ProIOC: ��ǰ�಻֧��ע��");
			return null;
		}
		try {
			Class<?> viewBindingClass = Class.forName(clsName + "$$ViewDataBinder");
			viewBinder = (ViewDataBinder) viewBindingClass.newInstance();
			if (debug)
				Log.d(TAG, "ProIOC: �ɹ�����ViewBinder.");
		} catch (ClassNotFoundException e) {
			if (debug)
				Log.w(TAG, "ʵ����ʧ�ܣ�������Ѱ " + cls.getSuperclass().getName());
			viewBinder = findVDBForClass(cls.getSuperclass());
		}
		DATABINDERS.put(cls, viewBinder);
		return viewBinder;
	}
	
	
}