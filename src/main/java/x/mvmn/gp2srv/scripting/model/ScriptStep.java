package x.mvmn.gp2srv.scripting.model;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;

import x.mvmn.gp2srv.camera.CameraService;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean;
import x.mvmn.jlibgphoto2.GP2Camera.GP2CameraEventType;

public class ScriptStep {

	public static enum ScriptStepType {
		CAPTURE, DELAY, CAMEVENT_WAIT, CAMPROP_SET, VAR_SET
	}

	protected ScriptStepType type;
	protected String key;
	protected String expression;
	protected String condition;

	protected transient volatile JexlExpression conditionExpressionCache;
	protected transient volatile JexlExpression expressionExpressionCache;

	public void execute(CameraService cameraService, JexlEngine engine, JexlContext context) {
		boolean execute = true;
		if (condition != null && !condition.trim().isEmpty()) {
			execute = Boolean
					.valueOf((conditionExpressionCache == null ? (conditionExpressionCache = engine.createExpression(condition)) : conditionExpressionCache)
							.evaluate(context).toString());
		}

		if (execute) {
			CameraConfigEntryBean configEntry = null;
			if (ScriptStepType.CAMPROP_SET.equals(type)) {
				configEntry = cameraService.getConfigAsMap().get(key.trim());
				if (configEntry != null) {
					context.set("__camprop", configEntry);
				}
			}

			Object evaluatedValue = (expressionExpressionCache == null ? (expressionExpressionCache = engine.createExpression(expression))
					: expressionExpressionCache).evaluate(context);
			String evaluatedValueAsString = evaluatedValue.toString();

			switch (type) {
				case CAPTURE:
					cameraService.capture();
				break;
				case DELAY:
					ensuredWait(Long.parseLong(evaluatedValueAsString));
				break;
				case CAMEVENT_WAIT:
					if (key != null && !key.trim().isEmpty()) {
						cameraService.waitForSpecificEvent(Integer.parseInt(evaluatedValueAsString),
								GP2CameraEventType.getByCode(Integer.parseInt(key.trim())));
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
		}
	}

	protected void ensuredWait(long totalWaitTime) {
		long waitTime = totalWaitTime;
		long startTime = System.currentTimeMillis();
		long projectedEndTime = startTime + totalWaitTime;
		do {
			try {
				if (waitTime > 0) {
					Thread.sleep(waitTime);
				}
			} catch (InterruptedException e) {
				// Wait time = Total wait time - passed time (e.g. total wait for 10 sec, interrupted after 3 sec, still 7 sec to wait)
				// Passed time = now - start time
				waitTime = totalWaitTime - (System.currentTimeMillis() - startTime);
			}
		} while (System.currentTimeMillis() < projectedEndTime);
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ScriptStep [type=").append(type).append(", key=").append(key).append(", expression=").append(expression).append(", condition=")
				.append(condition).append("]");
		return builder.toString();
	}
}
