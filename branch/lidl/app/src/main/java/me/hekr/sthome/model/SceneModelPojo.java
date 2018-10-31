package me.hekr.sthome.model;

/**
 * Created by jishu0001 on 2016/12/12.
 */
public class SceneModelPojo {
    private String length;
    private String sceneNum;
    private String sceneName;
    private String condition;
    private String timer;
    private String clickAction;
    private String inform;
    private String inputNum;
    private String outputNum;

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getSceneNum() {
        return sceneNum;
    }

    public void setSceneNum(String sceneNum) {
        this.sceneNum = sceneNum;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getClickAction() {
        return clickAction;
    }

    public void setClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public String getInform() {
        return inform;
    }

    public void setInform(String inform) {
        this.inform = inform;
    }

    public String getInputNum() {
        return inputNum;
    }

    public void setInputNum(String inputNum) {
        this.inputNum = inputNum;
    }

    public String getOutputNum() {
        return outputNum;
    }

    public void setOutputNum(String outputNum) {
        this.outputNum = outputNum;
    }

    @Override
    public String toString() {
        return "SceneModlePojo{" +
                "length='" + length + '\'' +
                ", sceneNum='" + sceneNum + '\'' +
                ", sceneName='" + sceneName + '\'' +
                ", condition='" + condition + '\'' +
                ", timer='" + timer + '\'' +
                ", clickAction='" + clickAction + '\'' +
                ", inform='" + inform + '\'' +
                ", inputNum='" + inputNum + '\'' +
                ", outputNum='" + outputNum + '\'' +
                '}';
    }
}
