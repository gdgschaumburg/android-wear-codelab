package com.example.android.wearable.recipeassistant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class Recipe {
    private static final String TAG = "RecipeAssistant";

    public String titleText;
    public String summaryText;
    public String recipeImage;
    public String ingredientsText;
    public String videoId;

    public static class RecipeStep {
        RecipeStep() { }
        public String stepImage;
        public String stepText;

        public Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.RECIPE_FIELD_STEP_TEXT, stepText);
            bundle.putString(Constants.RECIPE_FIELD_STEP_IMAGE, stepImage);
            return bundle;
        }

        public static RecipeStep fromBundle(Bundle bundle) {
            RecipeStep recipeStep = new RecipeStep();
            recipeStep.stepText = bundle.getString(Constants.RECIPE_FIELD_STEP_TEXT);
            recipeStep.stepImage = bundle.getString(Constants.RECIPE_FIELD_STEP_IMAGE);
            return recipeStep;
        }
    }
    ArrayList<RecipeStep> recipeSteps;

    public Recipe() {
        recipeSteps = new ArrayList<RecipeStep>();
    }

    public static Recipe fromJson(Context context, JSONObject json) {
        Recipe recipe = new Recipe();
        try {
            recipe.titleText = json.getString(Constants.RECIPE_FIELD_TITLE);
            recipe.summaryText = json.getString(Constants.RECIPE_FIELD_SUMMARY);
            recipe.videoId = json.getString(Constants.RECIPE_FIELD_VIDEO_ID);
            if (json.has(Constants.RECIPE_FIELD_IMAGE)) {
                recipe.recipeImage = json.getString(Constants.RECIPE_FIELD_IMAGE);
            }
            JSONArray ingredients = json.getJSONArray(Constants.RECIPE_FIELD_INGREDIENTS);
            recipe.ingredientsText = "";
            for (int i = 0; i < ingredients.length(); i++) {
                recipe.ingredientsText += " - "
                        + ingredients.getJSONObject(i).getString(Constants.RECIPE_FIELD_TEXT) + "\n";
            }

            JSONArray steps = json.getJSONArray(Constants.RECIPE_FIELD_STEPS);
            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                RecipeStep recipeStep = new RecipeStep();
                recipeStep.stepText = step.getString(Constants.RECIPE_FIELD_TEXT);
                if (step.has(Constants.RECIPE_FIELD_IMAGE)) {
                    recipeStep.stepImage = step.getString(Constants.RECIPE_FIELD_IMAGE);
                }
                recipe.recipeSteps.add(recipeStep);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error loading recipe: " + e);
            return null;
        }
        return recipe;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RECIPE_FIELD_TITLE, titleText);
        bundle.putString(Constants.RECIPE_FIELD_SUMMARY, summaryText);
        bundle.putString(Constants.RECIPE_FIELD_IMAGE, recipeImage);
        bundle.putString(Constants.RECIPE_FIELD_INGREDIENTS, ingredientsText);
        if (recipeSteps != null) {
            ArrayList<Parcelable> stepBundles = new ArrayList<Parcelable>(recipeSteps.size());
            for (RecipeStep recipeStep : recipeSteps) {
                stepBundles.add(recipeStep.toBundle());
            }
            bundle.putParcelableArrayList(Constants.RECIPE_FIELD_STEPS, stepBundles);
        }
        return bundle;
    }

    public static Recipe fromBundle(Bundle bundle) {
        Recipe recipe = new Recipe();
        recipe.titleText = bundle.getString(Constants.RECIPE_FIELD_TITLE);
        recipe.summaryText = bundle.getString(Constants.RECIPE_FIELD_SUMMARY);
        recipe.recipeImage = bundle.getString(Constants.RECIPE_FIELD_IMAGE);
        recipe.ingredientsText = bundle.getString(Constants.RECIPE_FIELD_INGREDIENTS);
        ArrayList<Parcelable> stepBundles =
                bundle.getParcelableArrayList(Constants.RECIPE_FIELD_STEPS);
        if (stepBundles != null) {
            for (Parcelable stepBundle : stepBundles) {
                recipe.recipeSteps.add(RecipeStep.fromBundle((Bundle) stepBundle));
            }
        }
        return recipe;
    }
}
