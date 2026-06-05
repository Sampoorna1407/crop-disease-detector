document.addEventListener("DOMContentLoaded", function () {

const API = "http://localhost:8080/api/v1/crops/analyze";

/* UI TRANSLATIONS */
const ui = {
    en: {title:"🌱 CropCare AI",subtitle:"Smart Crop Disease Detection",lang:"Language",crop:"Select Crop",analyze:"Analyze",symptoms:"Symptoms",treatment:"Treatment",confidence:"Confidence"},
    hi: {title:"🌱 क्रॉपकेयर एआई",subtitle:"फसल रोग पहचान",lang:"भाषा",crop:"फसल चुनें",analyze:"विश्लेषण करें",symptoms:"लक्षण",treatment:"उपचार",confidence:"विश्वास"},
    bn: {title:"🌱 ক্রপকেয়ার এআই",subtitle:"ফসল রোগ শনাক্তকরণ",lang:"ভাষা",crop:"ফসল নির্বাচন করুন",analyze:"বিশ্লেষণ করুন",symptoms:"লক্ষণ",treatment:"চিকিৎসা",confidence:"বিশ্বাস"},
    kn: {title:"🌱 ಕ್ರಾಪ್ ಕೇರ್ AI",subtitle:"ಬೆಳೆ ರೋಗ ಪತ್ತೆ",lang:"ಭಾಷೆ",crop:"ಬೆಳೆ ಆಯ್ಕೆ ಮಾಡಿ",analyze:"ವಿಶ್ಲೇಷಿಸಿ",symptoms:"ಲಕ್ಷಣಗಳು",treatment:"ಚಿಕಿತ್ಸೆ",confidence:"ನಂಬಿಕೆ"},
    ta: {title:"🌱 கிராப் கேர் AI",subtitle:"பயிர் நோய் கண்டறிதல்",lang:"மொழி",crop:"பயிரை தேர்ந்தெடுக்கவும்",analyze:"பகுப்பாய்வு",symptoms:"அறிகுறிகள்",treatment:"சிகிச்சை",confidence:"நம்பிக்கை"},
    te: {title:"🌱 క్రాప్ కేర్ AI",subtitle:"పంట వ్యాధి గుర్తింపు",lang:"భాష",crop:"పంటను ఎంచుకోండి",analyze:"విశ్లేషించండి",symptoms:"లక్షణాలు",treatment:"చికిత్స",confidence:"నమ్మకం"},
    or: {title:"🌱 କ୍ରପ୍କେର AI",subtitle:"ଫସଲ ରୋଗ ସନ୍ଦାନ",lang:"ଭାଷା",crop:"ଫସଲ ବାଛନ୍ତୁ",analyze:"ବିଶ୍ଳେଷଣ କରନ୍ତୁ",symptoms:"ଲକ୍ଷଣ",treatment:"ଚିକିତ୍ସା",confidence:"ଭରସା"}
};

/* APPLY LANGUAGE */
document.getElementById("languageSelect").onchange = function () {
    let lang = this.value;
    document.getElementById("title").innerText = ui[lang].title;
    document.getElementById("subtitle").innerText = ui[lang].subtitle;
    document.getElementById("langLabel").innerText = ui[lang].lang;
    document.getElementById("cropLabel").innerText = ui[lang].crop;
    document.getElementById("analyzeBtn").innerText = ui[lang].analyze;
    document.getElementById("symptomTitle").innerText = ui[lang].symptoms;
    document.getElementById("treatmentTitle").innerText = ui[lang].treatment;
};

/* TRANSLATE */
async function translateText(text, lang) {
    if (!text || lang === "en") return text;
    try {
        const res = await fetch(`https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=${lang}&dt=t&q=${encodeURIComponent(text)}`);
        const data = await res.json();
        return data[0][0][0] || text;
    } catch {
        return text;
    }
}

/* SMART TREATMENT */
function getAdvancedTreatment(disease) {

    disease = disease.toLowerCase();

    if (disease.includes("blight"))
        return ["Remove infected leaves","Use copper fungicide","Avoid overhead watering","Ensure airflow"];

    if (disease.includes("rust"))
        return ["Apply sulfur spray","Remove infected plants","Use resistant varieties"];

    if (disease.includes("spot"))
        return ["Use chlorothalonil spray","Avoid water splash","Improve drainage"];

    if (disease.includes("mildew"))
        return ["Apply potassium spray","Reduce humidity","Provide sunlight"];

    return ["Use balanced fertilizer","Monitor regularly","Consult expert"];
}

/* ANALYZE */
document.getElementById("analyzeBtn").onclick = async () => {

    const file = document.getElementById("fileInput").files[0];
    const crop = document.getElementById("cropType").value;
    const lang = document.getElementById("languageSelect").value;

    if (!file) {
        alert("Upload image first");
        return;
    }

    const formData = new FormData();
    formData.append("image", file);
    formData.append("cropType", crop);

    const res = await fetch(API, { method: "POST", body: formData });
    const data = await res.json();

    showResult(data, lang);
};

/* SHOW RESULT */
async function showResult(data, lang) {

    const resultCard = document.getElementById("resultCard");
    const preview = document.getElementById("previewImage");

    resultCard.hidden = false;

    const file = document.getElementById("fileInput").files[0];
    if (file) {
        preview.src = URL.createObjectURL(file);
        preview.hidden = false;
    }

    const disease = await translateText(data.diseaseName, lang);
    document.getElementById("diseaseName").innerText = disease;

    document.getElementById("confidence").innerText =
        ui[lang].confidence + ": " + Math.round(data.confidence * 100) + "%";

    let sym = document.getElementById("symptoms");
    sym.innerHTML = "";

    for (let s of (data.symptoms || [])) {
        let t = await translateText(s, lang);
        sym.innerHTML += `<li>${t}</li>`;
    }

    let tr = document.getElementById("treatments");
    tr.innerHTML = "";

    let treatments = getAdvancedTreatment(data.diseaseName);

    for (let t of treatments) {
        let tt = await translateText(t, lang);
        tr.innerHTML += `<li>${tt}</li>`;
    }
}

});