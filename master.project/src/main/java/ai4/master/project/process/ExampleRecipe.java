package ai4.master.project.process;

import ai4.master.project.recipe.Recipe;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

/**
 * Created by René Bärnreuther on 16.05.2017.
 * url: http://www.chefkoch.de/rezepte/2786421430769301/Vegetarisches-Fruehlingspfaennchen-mit-Feta-Kaese.html
 */
public class ExampleRecipe implements ProcessModeler {

    /*
    I didint put in any tools, just ingredients.
     */
    public BpmnModelInstance convertToProcess(Recipe recipe) {
        BpmnModelInstance bpmnModelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("zucchini")
                    .name("Zucchini waschen")
                    .camundaInputParameter("Zutat","Zucchini")
                    .camundaOutputParameter("Zutat","Gewaschene Zucchini")
                .userTask("karotte")
                    .name("Karotte schälen")
                    .camundaInputParameter("Zutat","Karotte")
                    .camundaOutputParameter("Zutat","Geschälte Karotte")
                .exclusiveGateway("schneiden_hobeln")
                    .userTask("laengs_schneiden")
                        .name("Beides längs schneiden")
                        .camundaInputParameter("Zutat", "Gewaschene Zucchini")
                        .camundaInputParameter("Zutat","Geschälte Karotte")
                        .camundaOutputParameter("Zutat","Geschnittene Zucchini")
                        .camundaOutputParameter("Zutat","Geschnittene Karotte")
                    .moveToLastGateway()
                    .userTask("laengs_hobeln")
                        .name("Beides längs hobeln")
                        .camundaInputParameter("Zutat", "Gewaschene Zucchini")
                        .camundaInputParameter("Zutat","Geschälte Karotte")
                        .camundaOutputParameter("Zutat","Geschnittene Zucchini")
                        .camundaOutputParameter("Zutat","Geschnittene Karotte")
                    .exclusiveGateway("schneiden_hobeln_end")
                    .moveToNode("laengs_schneiden")
                        .connectTo("schneiden_hobeln_end")
                    .moveToNode("laengs_hobeln")
                        .connectTo("schneiden_hobeln_end")
                .userTask("radieschen_putzen")
                    .name("Radieschen putzen")
                    .camundaInputParameter("Zutat","Radieschen")
                    .camundaOutputParameter("Zutat","Geputzte Radieschen")
                .userTask("radieschen_vierteln")
                    .name("Radieschen vierteln")
                    .camundaInputParameter("Zutat","Geputzte Radieschen")
                    .camundaOutputParameter("Zutat","Geviertelte Radieschen")
                .userTask("zwiebeln_schaelen")
                    .name("Zwiebeln schälen")
                    .camundaInputParameter("Zutat","Zwiebel")
                    .camundaOutputParameter("Zutat","Geschälte Zwiebel")
                .exclusiveGateway("zwiebel_teilen")
                .userTask("In_Ringe_schneiden")
                    .name("In Ringe schneiden")
                    .camundaInputParameter("Zutat","Geschälte Zwiebel")
                    .camundaOutputParameter("Zutat","Zwiebel in Ringe")
                    .moveToLastGateway()
                .userTask("In_Ringe_hobeln")
                    .name("In Ringe hobeln")
                    .camundaInputParameter("Zutat","Geschälte Zwiebel")
                    .camundaOutputParameter("Zutat","Zwiebel in Ringe")
                .exclusiveGateway("zwiebeln_teilen_end")
                .moveToNode("In_Ringe_schneiden")
                .connectTo("zwiebeln_teilen_end")
                .moveToNode("In_Ringe_hobeln")
                .connectTo("zwiebeln_teilen_end")
                .userTask("marinieren")
                    .name("Gemüse marinieren")
                    .camundaInputParameter("Zutat","Öl")
                    .camundaInputParameter("Zutat","Orangensaft")
                    .camundaInputParameter("Zutat","Rotweinessig")
                    .camundaOutputParameter("Zutat","Mariniertes Gemüse")
                .userTask("knoblauch_pressen")
                    .name("Knoblauch dazupressen")
                    .camundaInputParameter("Zutat","Knoblauch")
                    .camundaOutputParameter("Zutat","Mariniertes Gemüse mit Knoblauch")
                .userTask("wuerzen")
                    .name("Würzen")
                    .camundaInputParameter("Zutat","Kräutern")
                    .camundaInputParameter("Zutat","Salz")
                    .camundaInputParameter("Zutat","Pfeffer")
                    .camundaInputParameter("Zutat","Zucker")
                    .camundaOutputParameter("Zutat","Gewürztes Gemüse")
                .userTask("eingeben")
                    .name("In Ofenfeste Form geben")
                    .camundaInputParameter("Werkzeug","Ofenfeste Form")
                    .camundaOutputParameter("Zutat","Gemüse in ofenfester Form")
                .userTask("feta")
                    .name("Feta-Käse in Scheiben darauflegen")
                    .camundaInputParameter("Zutat","Feta-Käse in Scheiben")
                    .camundaOutputParameter("Zutat","Ofenfeste Form gefüllt")
                .userTask("ofen")
                    .name("In Ofen tun")
                    .boundaryEvent("timer")
                        .timerWithDuration("30")
                .endEvent()
                .done();

                Bpmn.validateModel(bpmnModelInstance);
                Bpmn.writeModelToStream(System.out,bpmnModelInstance);
                return bpmnModelInstance;
            }
}
