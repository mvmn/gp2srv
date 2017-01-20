package x.mvmn.gp2srv.scripting.model;

import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;

import x.mvmn.gp2srv.camera.CameraService;
import x.mvmn.gp2srv.scripting.service.impl.JexlMapContext;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean;
import x.mvmn.jlibgphoto2.CameraFileSystemEntryBean;
import x.mvmn.jlibgphoto2.GP2Camera.GP2CameraEventType;
import x.mvmn.lang.util.WaitUtil;

public class ScriptStep {

	public static enum ScriptStepType {
		CAPTURE(false), DELAY(true), CAMEVENT_WAIT(true), CAMPROP_SET(true), EXEC_SCRIPT(false), VAR_SET(true), STOP(false);

		protected final boolean usesExpression;

		ScriptStepType(boolean usesExpression) {
			this.usesExpression = usesExpression;
		}

		public boolean getUsesExpression() {
			return usesExpression;
		}
	}

	protected ScriptStepType type;
	protected String key;
	protected String expression;
	protected String condition;

	protected transient volatile JexlExpression conditionExpressionCache;
	protected transient volatile JexlExpression expressionExpressionCache;

	public ScriptStep() {
	}

	public ScriptStep(ScriptStepType type, String key, String expression, String condition) {
		this.setType(type);
		this.setKey(key);
		this.setExpression(expression);
		this.setCondition(condition);
	}

	public boolean evalCondition(JexlEngine engine, JexlMapContext context) {
		boolean execute = true;
		if (condition != null && !condition.trim().isEmpty()) {
			execute = Boolean
					.valueOf((conditionExpressionCache == null ? (conditionExpressionCache = engine.createExpression(condition)) : conditionExpressionCache)
							.evaluate(context).toString());
		}

		return execute;
	}

	public CameraConfigEntryBean getConfigEntryForEval(CameraService cameraService) {
		CameraConfigEntryBean configEntry = null;
		if (ScriptStepType.CAMPROP_SET.equals(type)) {
			configEntry = cameraService.getConfigAsMap().get(key.trim());
		}
		return configEntry;
	}

	public Object evalExpression(JexlEngine engine, JexlMapContext context, CameraConfigEntryBean configEntryForEval) {
		Object evaluatedValue = null;
		if (type.getUsesExpression()) {
			if (configEntryForEval != null) {
				context.set("__camprop", configEntryForEval);
			}
			evaluatedValue = (expressionExpressionCache == null ? (expressionExpressionCache = engine.createExpression(expression)) : expressionExpressionCache)
					.evaluate(context);
		}
		return evaluatedValue;
	}

	public boolean execute(CameraService cameraService, Object evaluatedValue, JexlEngine engine, JexlMapContext context, CameraConfigEntryBean configEntry) {
		boolean result = false;
		String evaluatedValueAsString = evaluatedValue != null ? evaluatedValue.toString() : "";

		switch (type) {
			case EXEC_SCRIPT:
				engine.createScript(expression).execute(context);
			break;
			case STOP:
				result = true;
			break;
			case CAPTURE:
				CameraFileSystemEntryBean cfseb = cameraService.capture();
				context.set("__capturedFile", cfseb.getPath() + (cfseb.getPath().endsWith("/") ? "" : "/") + cfseb.getName());
			break;
			case DELAY:
				WaitUtil.ensuredWait(Long.parseLong(evaluatedValueAsString));
			break;
			case CAMEVENT_WAIT:
				if (key != null && !key.trim().isEmpty()) {
					cameraService.waitForSpecificEvent(Integer.parseInt(evaluatedValueAsString), GP2CameraEventType.getByCode(Integer.parseInt(key.trim())));
				} else {
					cameraService.waitForEvent(Integer.parseInt(evaluatedValueAsString));
				}
			break;
			case VAR_SET:
				context.set(key, evaluatedValue);
			break;
			case CAMPROP_SET:
				if (configEntry != null) {
					switch (configEntry.getValueType()) {
						case FLOAT:
							configEntry = configEntry.cloneWithNewValue(Float.parseFloat(evaluatedValueAsString));
						break;
						case INT:
							configEntry = configEntry.cloneWithNewValue(Integer.parseInt(evaluatedValueAsString));
						break;
						case STRING:
							configEntry = configEntry.cloneWithNewValue(evaluatedValueAsString);
						break;
					}
					cameraService.setConfig(configEntry);
				}
			break;
		}
		return result;
	}

	public ScriptStepType getType() {
		return type;
	}

	public void setType(ScriptStepType type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ScriptStep [type=").append(type).append(", key=").append(key).append(", expression=").append(expression).append(", condition=")
				.append(condition).append("]");
		return builder.toString();
	}
}
