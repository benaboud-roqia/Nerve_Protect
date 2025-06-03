package com.dianerverotect.chatbot;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for handling medical AI model predictions
 */
public class MedicalAIModel {
    private static final String TAG = "MedicalAIModel";
    
    // Model types
    public static final int MODEL_ALS = 0;
    public static final int MODEL_NEUROPATHY = 1;
    
    // Model names
    private static final String ALS_MODEL_NAME = "alsnet3.tflite";
    private static final String NEUROPATHY_MODEL_NAME = "neuropathy_model.tflite";
    
    // TensorFlow Lite interpreters
    private Interpreter alsInterpreter;
    private Interpreter neuropathyInterpreter;
    
    // Context
    private final Context context;
    
    // Knowledge base for medical information
    private Map<String, String> knowledgeBase;
    
    /**
     * Constructor
     * @param context Application context
     */
    public MedicalAIModel(Context context) {
        this.context = context;
        initializeKnowledgeBase();
    }
    
    /**
     * Initialize the AI models
     * @return True if initialization was successful, false otherwise
     */
    public boolean initialize() {
        try {
            // Load ALS model
            alsInterpreter = new Interpreter(loadModelFile(ALS_MODEL_NAME));
            
            // Load neuropathy model
            neuropathyInterpreter = new Interpreter(loadModelFile(NEUROPATHY_MODEL_NAME));
            
            Log.d(TAG, "AI models loaded successfully");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error loading AI models: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get a response from the knowledge base
     * @param query User query
     * @param modelType Model type (ALS or neuropathy)
     * @return Response from the knowledge base
     */
    public String getResponse(String query, int modelType) {
        // Normalize query
        String normalizedQuery = query.toLowerCase();
        
        // Check knowledge base for direct answers
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (normalizedQuery.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // If no direct answer, generate a response based on the model type
        if (modelType == MODEL_ALS) {
            return generateALSResponse(normalizedQuery);
        } else if (modelType == MODEL_NEUROPATHY) {
            return generateNeuropathyResponse(normalizedQuery);
        } else {
            return "I'm not sure how to answer that question. Could you please ask something about ALS or neuropathy?";
        }
    }
    
    /**
     * Generate a response for ALS queries
     * @param query User query
     * @return Response for ALS query
     */
    private String generateALSResponse(String query) {
        // In a real implementation, this would use the ALS model for more sophisticated responses
        // For now, we'll use pre-defined responses based on keywords
        
        if (query.contains("what is als") || query.contains("define als")) {
            return "Amyotrophic Lateral Sclerosis (ALS) is a progressive neurodegenerative disease that affects nerve cells in the brain and spinal cord. It causes the death of neurons that control voluntary muscles, leading to muscle weakness, paralysis, and eventually respiratory failure.";
        } else if (query.contains("symptom") || query.contains("sign")) {
            return "Common symptoms of ALS include:\n• Progressive muscle weakness\n• Muscle twitching (fasciculations)\n• Muscle cramping\n• Stiff muscles (spasticity)\n• Difficulty speaking or swallowing\n• Shortness of breath\n\nSymptoms typically begin in the limbs, but can also start with speech or swallowing difficulties (bulbar onset).";
        } else if (query.contains("cause") || query.contains("risk factor")) {
            return "The exact cause of ALS is unknown for most cases. About 5-10% of cases are inherited (familial ALS). Risk factors include:\n• Family history of ALS\n• Age (most commonly develops between ages 40-70)\n• Environmental factors (possible links to lead exposure, military service)\n• Smoking\n\nGenetic mutations, particularly in the C9ORF72, SOD1, FUS, and TARDBP genes, have been identified in familial cases.";
        } else if (query.contains("treatment") || query.contains("cure") || query.contains("manage")) {
            return "There is currently no cure for ALS. Treatment focuses on managing symptoms and improving quality of life:\n• FDA-approved medications: Riluzole and Edaravone can slow disease progression\n• Physical therapy to maintain muscle strength and function\n• Occupational therapy for daily activities\n• Speech therapy for communication\n• Respiratory support as the disease progresses\n• Nutritional support\n\nMultidisciplinary care teams typically provide the best outcomes for patients.";
        } else if (query.contains("diagnosis") || query.contains("test") || query.contains("emg")) {
            return "ALS diagnosis involves ruling out other conditions with similar symptoms. The diagnostic process typically includes:\n• Detailed medical history and neurological examination\n• Electromyography (EMG) to measure electrical activity in muscles\n• Nerve conduction studies\n• MRI scans to rule out other conditions\n• Blood and urine tests\n• Sometimes muscle or nerve biopsies\n• Genetic testing for familial ALS\n\nDiagnosis often takes time as symptoms develop and other conditions are ruled out.";
        } else if (query.contains("prognosis") || query.contains("life expectancy") || query.contains("survival")) {
            return "ALS is a progressive disease with varying rates of progression. The average life expectancy after diagnosis is about 2-5 years, but many people live longer:\n• About 20% of people live 5 years or more\n• Up to 10% survive 10 years or more\n• Physicist Stephen Hawking lived with ALS for over 50 years\n\nFactors affecting prognosis include age at onset, site of onset (bulbar onset typically has worse prognosis), and rate of progression.";
        } else {
            return "I understand you're asking about ALS. Could you please be more specific about what information you need regarding ALS? I can provide information about symptoms, diagnosis, treatment options, or research developments.";
        }
    }
    
    /**
     * Generate a response for neuropathy queries
     * @param query User query
     * @return Response for neuropathy query
     */
    private String generateNeuropathyResponse(String query) {
        // In a real implementation, this would use the neuropathy model for more sophisticated responses
        // For now, we'll use pre-defined responses based on keywords
        
        if (query.contains("what is neuropathy") || query.contains("define neuropathy")) {
            return "Neuropathy refers to damage or dysfunction of one or more nerves that typically results in numbness, tingling, muscle weakness, and pain in the affected area. Diabetic neuropathy is a type of nerve damage that can occur if you have diabetes, most commonly affecting the legs and feet.";
        } else if (query.contains("symptom") || query.contains("sign")) {
            return "Common symptoms of diabetic neuropathy include:\n• Numbness or reduced ability to feel pain or temperature changes\n• Tingling or burning sensation\n• Sharp pains or cramps\n• Increased sensitivity to touch\n• Muscle weakness\n• Loss of balance and coordination\n• Foot problems such as ulcers, infections, and joint pain";
        } else if (query.contains("cause") || query.contains("risk factor")) {
            return "Diabetic neuropathy is caused by prolonged high blood sugar levels that damage nerves. Risk factors include:\n• Poor blood sugar control\n• Duration of diabetes (risk increases the longer you have diabetes)\n• Kidney disease\n• Being overweight\n• Smoking\n• High blood pressure\n• High cholesterol";
        } else if (query.contains("treatment") || query.contains("cure") || query.contains("manage")) {
            return "Treatment for diabetic neuropathy focuses on:\n• Blood glucose management to prevent further nerve damage\n• Pain management with medications like gabapentin, pregabalin, or duloxetine\n• Topical treatments like capsaicin cream\n• Physical therapy\n• Foot care to prevent complications\n• Lifestyle modifications including regular exercise and healthy diet\n\nWhile there is no cure, proper management can slow progression and relieve symptoms.";
        } else if (query.contains("diagnosis") || query.contains("test")) {
            return "Diagnosis of diabetic neuropathy typically involves:\n• Physical examination\n• Medical history review\n• Neurological examination to check reflexes, muscle strength, and sensitivity to touch and vibration\n• Nerve conduction studies to measure how quickly nerves conduct electrical signals\n• Electromyography (EMG) to evaluate electrical discharges in muscles\n• Sometimes nerve or skin biopsies\n\nRegular screening is recommended for people with diabetes.";
        } else if (query.contains("prevention") || query.contains("prevent")) {
            return "To prevent or delay diabetic neuropathy:\n• Maintain target blood glucose levels\n• Follow a healthy diet\n• Exercise regularly\n• Maintain a healthy weight\n• Avoid smoking and excessive alcohol\n• Monitor your blood glucose levels regularly\n• Attend regular check-ups with your healthcare provider\n• Take good care of your feet with daily inspections and proper footwear";
        } else {
            return "I understand you're asking about neuropathy. Could you please be more specific about what information you need regarding neuropathy? I can provide information about symptoms, diagnosis, treatment options, or prevention strategies.";
        }
    }
    
    /**
     * Load a TensorFlow Lite model file from assets
     * @param modelName Name of the model file
     * @return MappedByteBuffer containing the model
     * @throws IOException If the model file cannot be loaded
     */
    private MappedByteBuffer loadModelFile(String modelName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(context.getAssets().openFd(modelName).getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = context.getAssets().openFd(modelName).getStartOffset();
        long declaredLength = context.getAssets().openFd(modelName).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    
    /**
     * Initialize the knowledge base with medical information
     */
    private void initializeKnowledgeBase() {
        knowledgeBase = new HashMap<>();
        
        // ALS knowledge
        knowledgeBase.put("what is als", "Amyotrophic Lateral Sclerosis (ALS) is a progressive neurodegenerative disease that affects nerve cells in the brain and spinal cord, leading to loss of muscle control.");
        knowledgeBase.put("als symptoms", "Common symptoms of ALS include muscle weakness, muscle twitching (fasciculations), stiff muscles (spasticity), and difficulty speaking or swallowing.");
        knowledgeBase.put("als treatment", "There is no cure for ALS, but treatments like riluzole and edaravone can help slow disease progression. Supportive care is also important.");
        knowledgeBase.put("als causes", "The exact cause of ALS is unknown in most cases. About 5-10% of cases are inherited (familial ALS).");
        knowledgeBase.put("als diagnosis", "ALS diagnosis involves ruling out other conditions through neurological exams, EMG, nerve conduction studies, MRI, and sometimes muscle or nerve biopsies.");
        
        // Neuropathy knowledge
        knowledgeBase.put("what is neuropathy", "Neuropathy refers to damage or dysfunction of peripheral nerves, leading to numbness, tingling, pain, and weakness, commonly affecting the hands and feet.");
        knowledgeBase.put("diabetic neuropathy", "Diabetic neuropathy is nerve damage caused by diabetes, most commonly affecting the legs and feet, resulting from prolonged high blood sugar levels.");
        knowledgeBase.put("neuropathy symptoms", "Common symptoms of neuropathy include numbness, tingling, burning or stabbing pain, sensitivity to touch, muscle weakness, and problems with balance.");
        knowledgeBase.put("neuropathy treatment", "Treatment for neuropathy includes managing the underlying cause, pain medications, physical therapy, and lifestyle modifications.");
        knowledgeBase.put("neuropathy causes", "Neuropathy can be caused by diabetes, infections, autoimmune diseases, medications, trauma, vitamin deficiencies, and alcoholism.");
        knowledgeBase.put("neuropathy prevention", "Preventing neuropathy involves managing underlying conditions like diabetes, maintaining a healthy lifestyle, avoiding toxin exposure, and regular check-ups.");
        
        // EMG related knowledge
        knowledgeBase.put("what is emg", "Electromyography (EMG) is a diagnostic procedure that evaluates the health of muscles and the nerve cells that control them. It's commonly used in diagnosing both ALS and neuropathy.");
        knowledgeBase.put("emg test", "An EMG test involves inserting a needle electrode into specific muscles to record electrical activity. It helps diagnose nerve and muscle disorders like ALS and neuropathy.");
        knowledgeBase.put("emg als", "In ALS, EMG typically shows evidence of widespread motor neuron degeneration, with signs of denervation and reinnervation in multiple body regions.");
        knowledgeBase.put("emg neuropathy", "In neuropathy, EMG can show reduced nerve conduction velocity and amplitude, helping to determine the type and severity of nerve damage.");

        // ALS and Neuropathy Relationship
        knowledgeBase.put("can diabetes cause als", "Diabetes does not cause ALS directly. However, a diabetic patient may rarely develop ALS independently, as the two conditions affect different parts of the nervous system.");
        knowledgeBase.put("difference between als and diabetic neuropathy", "ALS is a motor neuron disease affecting voluntary muscles, while diabetic neuropathy is a peripheral nerve condition caused by prolonged high blood sugar.");
        knowledgeBase.put("can als be confused with diabetic neuropathy", "Yes, early ALS and diabetic neuropathy may both present with muscle weakness. However, ALS causes progressive motor loss, while neuropathy often begins with sensory symptoms like tingling or burning.");

// Daily Symptoms and Monitoring
        knowledgeBase.put("burning feet at night", "Burning sensation in the feet at night is a common symptom of peripheral neuropathy, often linked to diabetes-related nerve damage.");
        knowledgeBase.put("muscle twitching causes", "Muscle twitching or fasciculations can occur in ALS, but may also result from fatigue, stress, or benign conditions.");
        knowledgeBase.put("will i be paralyzed", "In ALS, paralysis can develop as the disease progresses. In diabetic neuropathy, paralysis is rare, but mobility may be reduced due to pain or weakness.");
        knowledgeBase.put("can i still work with neuropathy", "Many patients with mild to moderate diabetic neuropathy can continue working. Severe cases or ALS may require job adjustments or leave.");
        knowledgeBase.put("is neuropathy pain in my head", "No, the pain is real and due to nerve dysfunction. Neuropathic pain may feel like burning, tingling, or stabbing and needs proper treatment.");

// Diagnostic Testing
        knowledgeBase.put("can mri detect als or neuropathy", "MRI cannot directly detect ALS or neuropathy but is used to rule out other conditions. EMG is the main tool for diagnosing both.");
        knowledgeBase.put("how long does als diagnosis take", "ALS diagnosis can take several months due to the need to rule out other conditions through clinical evaluation, EMG, and lab tests.");
        knowledgeBase.put("can i have both als and diabetic neuropathy", "It is rare, but a person can have both ALS and diabetic neuropathy. A neurologist can differentiate between them using diagnostic tests.");
        knowledgeBase.put("can neuropathy be cured", "Neuropathy cannot be cured once the nerves are damaged, but symptoms can be managed and progression slowed with proper treatment.");
        knowledgeBase.put("is als hereditary", "About 5-10% of ALS cases are hereditary. Most are sporadic and have no clear family history.");

// Lifestyle and Environment
        knowledgeBase.put("should i change my diet", "Yes. A healthy, balanced diet helps manage diabetes and supports overall nerve health. ALS patients benefit from nutrition that maintains strength and weight.");
        knowledgeBase.put("can i drive with neuropathy", "If your reflexes and foot sensitivity are impaired, it may not be safe to drive. A medical evaluation is recommended.");
        knowledgeBase.put("should i see a neurologist", "If you experience persistent numbness, weakness, or muscle twitching, it's advisable to consult a neurologist for evaluation.");
        knowledgeBase.put("does stress make symptoms worse", "Yes. Stress can intensify symptoms like neuropathic pain and fatigue. Relaxation techniques and mental health support are helpful.");
        knowledgeBase.put("are painkillers effective for neuropathy", "Common painkillers often do not relieve neuropathic pain. Medications like gabapentin, pregabalin, or certain antidepressants are typically prescribed.");

// Glycemia, Temperature & Activity
        knowledgeBase.put("high blood sugar and nerve pain", "High blood sugar damages nerves over time and can worsen symptoms like burning, numbness, and pain in diabetic neuropathy.");
        knowledgeBase.put("how temperature affects neuropathy", "Cold can worsen pain and slow nerve conduction. Warm temperatures may provide some relief but excessive heat should be avoided.");
        knowledgeBase.put("can i exercise with neuropathy", "Yes, light to moderate exercise is beneficial. Walking, swimming, and stretching improve circulation and nerve health.");
        knowledgeBase.put("what to do if blood sugar is low after exercise", "If your blood sugar drops below 70 mg/dL after exercise, consume a fast-acting carbohydrate like juice or glucose tablets and recheck levels in 15 minutes.");
        knowledgeBase.put("what if i feel worse after walking", "You may have overexerted. Check your feet for injury and your blood sugar levels. Reduce activity and consult your doctor if symptoms persist.");


// Daily Tracking & Follow-Up
        knowledgeBase.put("should i monitor my symptoms daily", "Yes, daily symptom tracking helps detect progression and allows your healthcare provider to adjust treatment proactively.");
        knowledgeBase.put("what questions should i ask my neurologist", "Ask about your diagnosis, progression expectations, treatment options, need for further tests, and advice on daily management.");
        knowledgeBase.put("what should i report to the chatbot daily", "Report your activity level, symptoms (numbness, weakness, pain), blood sugar levels, sleep quality, and emotional state.");

        knowledgeBase.put("can als cause fatigue", "Yes, fatigue is a common symptom of ALS and can be caused by muscle weakness, respiratory strain, or disrupted sleep.");
        knowledgeBase.put("is als painful", "Pain is not typically an early symptom of ALS, but muscle cramps, immobility, and joint stiffness in later stages can cause discomfort.");
        knowledgeBase.put("what are late-stage symptoms of als", "Late-stage ALS symptoms include total loss of voluntary muscle control, respiratory failure, and difficulty eating and speaking.");
        knowledgeBase.put("can als affect memory", "ALS primarily affects motor neurons. Cognitive impairment is rare but can occur in some cases, especially when associated with frontotemporal dementia.");
        knowledgeBase.put("how is als different from ms", "ALS affects motor neurons and leads to muscle wasting; MS affects the central nervous system and causes demyelination with more varied symptoms.");
        knowledgeBase.put("is als more common in men or women", "ALS is slightly more common in men than in women, especially in cases of earlier onset.");
        knowledgeBase.put("can als affect young adults", "ALS is more common after age 40, but it can rarely affect younger individuals, a form known as juvenile ALS.");
        knowledgeBase.put("is als contagious", "No, ALS is not contagious. It cannot be transmitted from one person to another.");

        knowledgeBase.put("can neuropathy affect digestion", "Yes, autonomic neuropathy can affect the nerves that control digestion, leading to symptoms like nausea, bloating, or constipation.");
        knowledgeBase.put("does neuropathy cause muscle cramps", "Yes, neuropathy can lead to muscle cramps due to nerve dysfunction and poor coordination of muscle signals.");
        knowledgeBase.put("can neuropathy affect hearing or vision", "While rare, certain types of neuropathy (like cranial neuropathy) can affect hearing or vision.");
        knowledgeBase.put("how does neuropathy affect sleep", "Neuropathic pain or discomfort, especially at night, can interfere with falling or staying asleep.");
        knowledgeBase.put("is walking good for neuropathy", "Yes, regular walking helps improve circulation, reduce pain, and preserve mobility in people with neuropathy.");
        knowledgeBase.put("can neuropathy cause incontinence", "Yes, autonomic neuropathy can affect bladder control and cause incontinence or urinary urgency.");
        knowledgeBase.put("can neuropathy be caused by chemotherapy", "Yes, some chemotherapy drugs can damage peripheral nerves, causing chemotherapy-induced peripheral neuropathy (CIPN).");
        knowledgeBase.put("how is neuropathy diagnosed", "Neuropathy is diagnosed using clinical exams, blood tests, EMG, nerve conduction studies, and sometimes skin or nerve biopsies.");

        knowledgeBase.put("how to manage als at home", "Management includes physical therapy, assistive devices, nutritional support, respiratory care, and caregiver coordination.");
        knowledgeBase.put("how to relieve neuropathic pain naturally", "Options include regular exercise, acupuncture, warm baths, meditation, and dietary supplements like alpha-lipoic acid.");
        knowledgeBase.put("is there a blood test for als", "No single blood test can diagnose ALS, but blood tests help rule out other conditions that mimic ALS symptoms.");
        knowledgeBase.put("can both als and neuropathy cause foot drop", "Yes, both can cause foot drop due to motor nerve involvement, but ALS typically affects more regions progressively.");
        knowledgeBase.put("how can i protect my nerves", "Maintain stable blood sugar, avoid toxins, exercise, manage stress, and get regular medical checkups.");
        knowledgeBase.put("are als and neuropathy curable", "Neither ALS nor most chronic neuropathies are curable, but treatments can slow progression and relieve symptoms.");
        knowledgeBase.put("what is nerve regeneration", "Nerve regeneration refers to the healing and regrowth of nerve fibers, which is limited in motor neurons but more possible in peripheral nerves.");
        knowledgeBase.put("does physical therapy help in als", "Yes, physical therapy helps maintain mobility and reduce stiffness and pain, even though it does not stop progression.");
        knowledgeBase.put("how often should i check my blood sugar if i have neuropathy", "You should follow your doctor’s recommendations, but typically 3-5 times daily, especially if symptoms fluctuate.");
        knowledgeBase.put("can als cause numbness", "Numbness is not a typical ALS symptom; it suggests sensory nerve involvement, which is more common in neuropathy.");

        knowledgeBase.put("can i exercise with als", "Yes, light stretching and range-of-motion exercises are recommended in early ALS stages, but should be guided by a physical therapist.");
        knowledgeBase.put("what exercise is good for neuropathy", "Low-impact activities like walking, swimming, or cycling help maintain nerve function and reduce pain.");
        knowledgeBase.put("should i rest more if i have als", "Yes, conserving energy is important in ALS. Alternate activity with rest to avoid fatigue.");
        knowledgeBase.put("can i go outside with neuropathy", "Yes, but take precautions such as supportive footwear and checking foot temperature and injury risk.");
        knowledgeBase.put("is physical therapy helpful for neuropathy", "Yes, it improves strength, coordination, and reduces pain in many neuropathic conditions.");

        knowledgeBase.put("what foods are good for neuropathy", "Foods rich in B vitamins, omega-3 fatty acids, and antioxidants help support nerve health.");
        knowledgeBase.put("can poor diet worsen neuropathy", "Yes, nutritional deficiencies can aggravate nerve damage, especially lack of B12 or folate.");
        knowledgeBase.put("should i eat differently if i have als", "Yes, ALS patients should follow a high-calorie, high-protein diet to maintain weight and muscle mass.");
        knowledgeBase.put("does sugar affect neuropathy", "Yes, high blood sugar can worsen neuropathic pain and nerve damage. Keep glucose levels controlled.");

        knowledgeBase.put("what is foot drop", "Foot drop is a condition where lifting the front of the foot is difficult, seen in both ALS and advanced neuropathy.");
        knowledgeBase.put("can als start in the legs", "Yes, ALS can begin in the legs, hands, or bulbar muscles depending on the variant.");
        knowledgeBase.put("what is the first sign of neuropathy", "Tingling, burning, or numbness in the feet or hands are often early signs.");
        knowledgeBase.put("can als cause hand weakness", "Yes, hand weakness and muscle atrophy are common early signs of ALS.");
        knowledgeBase.put("should i see a neurologist for tingling", "Yes, persistent tingling may indicate a neurological condition and should be evaluated.");

        knowledgeBase.put("what tests confirm neuropathy", "Neuropathy is diagnosed using EMG, nerve conduction studies, blood tests, and sometimes skin or nerve biopsies.");
        knowledgeBase.put("is emg better than mri for als", "EMG is more specific for ALS, showing muscle denervation, while MRI is used to exclude other conditions.");
        knowledgeBase.put("can blood tests show neuropathy", "Blood tests can suggest underlying causes (e.g., diabetes, vitamin deficiency), but not neuropathy itself.");
        knowledgeBase.put("how often should i do emg", "Your neurologist will decide; EMG is not done frequently unless symptoms are changing or unclear.");
        knowledgeBase.put("can mri detect muscle wasting", "MRI can show muscle atrophy, but it is not the primary tool for diagnosing ALS or neuropathy.");


        knowledgeBase.put("can stress make neuropathy worse", "Yes, stress can amplify pain perception and worsen symptoms. Relaxation techniques may help.");
        knowledgeBase.put("does als affect mood", "ALS can cause emotional lability or depression. Psychological support is important in care.");
        knowledgeBase.put("how to sleep better with neuropathy", "Sleep hygiene, pain control, and low-stimulation environments can help improve sleep with neuropathy.");
        knowledgeBase.put("can anxiety cause neuropathy symptoms", "Stress and anxiety can mimic or exacerbate neuropathic sensations, but true neuropathy requires nerve damage.");
        knowledgeBase.put("should i join a support group for als", "Yes, support groups provide emotional comfort, information, and community during ALS progression.");



    }
    
    /**
     * Close the interpreters
     */
    public void close() {
        if (alsInterpreter != null) {
            alsInterpreter.close();
            alsInterpreter = null;
        }
        
        if (neuropathyInterpreter != null) {
            neuropathyInterpreter.close();
            neuropathyInterpreter = null;
        }
    }
}
