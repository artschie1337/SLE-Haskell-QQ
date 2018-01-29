(ns metadocs.pages.contribution
  (:require [cljsjs.filesaverjs]
            [reagent.core :as reagent]
            [metadocs.components.section :as section]
            [metadocs.components.summary :as summary]))

(defn model->blob [model]
  (let [json #js [(-> @model clj->js (js/JSON.stringify nil 4))]]
    (-> json (js/Blob. json #js {:type "application/json;charset=utf-8"}))))

(defn move-up [sections key]
  (when (> key 0)
    (let [previous-section (get @sections (dec key))
          section (get @sections key)]
      (swap! sections assoc key previous-section)
      (swap! sections assoc (dec key) section))))

(defn move-down [sections key]
  (when (< key (dec (count @sections)))
    (let [next-section (get @sections (inc key))
          section (get @sections key)]
      (swap! sections assoc key next-section)
      (swap! sections assoc (inc key) section))))

(defn add-section [sections]
  (swap! sections conj {:features []
                        :headline "New section"
                        :perspectives []
                        :languages []
                        :technologies []
                        :concepts []
                        :artifacts []}))

(defn page [model]
  (let [model (reagent/atom model)
        sections (reagent/cursor model [:sections])
        name (reagent/cursor model [:name])
        baseuri (reagent/cursor model [:baseuri])
        headline (reagent/cursor model [:headline])
        perspectives (sort (distinct (mapcat :perspectives @sections)))
        features (sort (distinct (mapcat :features @sections)))
        languages (sort (distinct (mapcat :languages @sections)))
        technologies (sort (distinct (mapcat :technologies @sections)))
        concepts (sort (distinct (mapcat :concepts @sections)))]
    (fn []
      [:div
       [:div {:class "hero is-primary"}
        [:div {:class "hero-body"}
         [:div {:class "container"}
          [:h1 {:class "title"} @headline]]]]
       [:section {:class "section"}
        [:div {:class "container"}
         [:div {:class "field is-horizontal"}
          [:div {:class "field-label is-normal"}
           [:label {:class "label"} "Contribution name"]]
          [:div {:class "field-body"}
           [:div {:class "field"}
            [:p {:class "control"}
             [:input {:class "input" :type "text" :on-change #(->> % .-target .-value (reset! name)) :value @name}]]]]]
         [:div {:class "field is-horizontal"}
          [:div {:class "field-label is-normal"}
           [:label {:class "label"} "Headline"]]
          [:div {:class "field-body"}
           [:div {:class "field"}
            [:p {:class "control"}
             [:input {:class "input" :type "text" :on-change #(->> % .-target .-value (reset! headline)) :value @headline}]]]]]
         [:div {:class "field is-horizontal"}
          [:div {:class "field-label is-normal"}
           [:label {:class "label"} "Baseuri"]]
          [:div {:class "field-body"}
           [:div {:class "field"}
            [:p {:class "control"}
             [:input {:class "input" :type "url" :on-change #(->> % .-target .-value (reset! baseuri)) :value @baseuri}]]]]]
         [:div {:class "field is-grouped is-grouped-right"}
          [:div {:class "control"}
           [:button {:class "button is-large" :on-click #(-> model model->blob (js/saveAs (str @name ".json")))}
            [:span {:class "icon is-large"}
             [:i {:class "far fa-arrow-alt-circle-down"}]]
            [:span "Download"]]]]]]
       (map #(let [section (reagent/cursor sections [%])]
               (with-meta [:section {:class "section"}
                           [:div {:class "container"}
                            [section/component baseuri section (partial move-up sections %) (partial move-down sections %) (= % 0) (= % (dec (count @sections)))]]] {:key %}))
            (range (count @sections)))
       [:section {:class "section"}
        [:div {:class "container"}
         [:div {:class "columns is-centered"}
          [:div {:class "column is-half is-narrow"}
           [:button {:class "button is-large" :on-click (partial add-section sections)}
            [:span {:class "icon is-large"}
             [:i {:class "far fa-plus-square"}]]]]]]]])))

