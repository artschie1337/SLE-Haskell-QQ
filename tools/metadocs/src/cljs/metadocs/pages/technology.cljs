(ns metadocs.pages.technology
  (:require [metadocs.config :as config]
            [metadocs.utils.url :as url]))

(defn page [technology contributions]
  (let [wiki-url (str config/wiki-url "Technology:" technology)]
    [:div
     [:section {:class "section"}
      [:div {:class "container"}
       [:h2 {:class "title is-2"}
        "Technology: " technology]]]
     [:section {:class "section"}
      [:div {:class "container"}
       [:h3 {:class "title is-3"}
        "Contributions"]
       [:ul (map #(with-meta [:li [:a {:href (str "/metalib/contributions/" (url/sanitize %) ".html")} %]] {:key %}) contributions)]]]
     [:section {:class "section"}
      [:div {:class "container"}
       [:h3 {:class "title is-3"}
        "101Wiki"]
       [:a {:href wiki-url :target "_blank"} wiki-url]]]]))

