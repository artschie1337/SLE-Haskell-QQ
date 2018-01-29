(ns metadocs.components.artifact
  (:require [cljs-http.client :as http]
            [clojure.string :as string]
            [metadocs.config :as config]
            [metadocs.utils.code :as code]
            [metadocs.utils.github :as github]
            [metadocs.wrapper.highlight :as highlight]
            [reagent.core :as reagent])
  (:require-macros [cljs.core.async.macros :as async]))

(defmulti component (fn [_ artifact] (:type @artifact)))

(defmethod component "none" [baseuri artifact]
  (let [link (:link @artifact)
        link-edit? (reagent/atom false)]
    (fn []
      [:div {:class "levels"}
       [:div {:class "level-left"}
        [:div {:class "level-item"}
         [:a {:href (str @baseuri link) :target "_blank"} link]]]])))

(defmethod component "all" [baseuri artifact]
  (let [link (:link @artifact)
        link-edit? (reagent/atom false)]
    (fn []
      [:div
       [:div {:class "levels"}
        [:div {:class "level-left"}
         [:div {:class "level-item"}
          [:a {:href (str @baseuri link) :target "_blank"} link]]]]
       (if (some true? (map #(string/ends-with? (string/lower-case link) %) config/bin-eols))
         [:img {:src (github/to-raw-uri (str @baseuri link))}]
         [(let [snippet (reagent/atom nil)]
            (async/go (let [response (<! (http/get (github/to-raw-uri (str @baseuri link)) {:with-credentials? false}))]
                        (reset! snippet (code/elide (:body response)))))
            (fn []
              (if (nil? @snippet)
                [:div]
                [:div
                 [(highlight/wrapper [:pre @snippet])]])))])])))

(defmethod component "some" [baseuri artifact]
  (let [from (:from @artifact)
        to (:to @artifact)
        link (:link @artifact)
        snippet (reagent/atom nil)
        is-edit? (reagent/atom false)]
    (async/go (let [response (<! (http/get (github/to-raw-uri (str @baseuri link)) {:with-credentials? false}))]
                (reset! snippet (code/elide (code/strip (:body response) from to)))))
    (fn []
      [:div
       (if @is-edit?
         [:div {:class "levels"}
          [:div {:class "level-left"}
           [:div {:class "level-item"}
            [:input {:class "input" :type "number" :on-change #(->> % .-target .-value js/parseInt (swap! artifact update :from))}]]]]
         [:div {:class "levels"}
          [:div {:class "level-left"}
           [:div {:class "level-item"}
            [:a {:href (str @baseuri link) :target "_blank"} link]]]])
       (if (nil? @snippet)
         [:div]
         [:div
          [(highlight/wrapper [:pre @snippet])]])])))
