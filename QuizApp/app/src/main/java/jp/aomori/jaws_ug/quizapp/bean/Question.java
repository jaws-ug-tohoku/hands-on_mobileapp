package jp.aomori.jaws_ug.quizapp.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Question {

    private int count;
    private String sentence;
    private List<QuestionItem> questions;

    public Question(String jsonText) {
        this.questions = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonText);
            JSONArray qArray = jsonObject.getJSONArray("questions");

            this.count = jsonObject.getInt("count");
            this.sentence = jsonObject.getString("sentence");

            for (int i = 0; i < this.count; i++) {
                JSONObject item = qArray.getJSONObject(i);

                QuestionItem questionItem = new QuestionItem();
                questionItem.setServiceId(item.getString("serviceId"));
                questionItem.setCategory(item.getString("category"));
                questionItem.setLabel(item.getString("label"));
                questionItem.setFilename(item.getString("filename"));
                questionItem.setDescription(item.getString("description"));
                questionItem.setCaption(item.getString("caption"));

                this.questions.add(questionItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getCount() {
        return count;
    }

    public String getSentence() {
        return sentence;
    }

    public QuestionItem getQuestionItem(int index) {
        if (this.questions != null) {
            return this.questions.get(index);
        }

        return null;
    }


}
