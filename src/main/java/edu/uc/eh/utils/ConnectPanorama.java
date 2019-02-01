package edu.uc.eh.utils;


import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.domain.GctFile;
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.query.SelectRowsCommand;
import org.labkey.remoteapi.query.SelectRowsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by chojnasm on 7/13/15.
 */

@Service
public class ConnectPanorama {

    private static final Logger log = LoggerFactory.getLogger(ConnectPanorama.class);

    @Value("${panorama.folders}")
    private String panoramaFolders;// = "LINCS/P100,LINCS/GCP";

    @Value("${panorama.runIdUrl}")
    private String runIdUrl;//="https://panoramaweb.org/labkey/targetedms/LINCS/%s/showPrecursorList.view?id=%d";

    @Value("${panorama.gctDownloadUrl}")
    private String gctDownloadUrl;

    @Value("${panorama.gctDownloadUrl2}")
    private String gctDownloadUrl2;

    @Value("${panorama.connectionUrl}")
    private String panoramaConnectionUrl;

    @Value("${panorama.peptideInternalIdUrl}")
    private String peptideInternalIdsUrl;

    @Value("${panorama.chromatogramUrl}")
    private String chromatogramUrl;

    @Value("${panorama.peptideAnnotationsUrl}")
    private String peptideAnnotationsUrl;

    @Value("${panorama.replicateAnnotationsUrl}")
    private String replicateAnnotationsUrl;

    public List<String> gctDownloadUrls(boolean ifProcessed) throws IOException, CommandException {

        List<String> output = new ArrayList<>();
        String[] folderNames = panoramaFolders.split(",");
        String gcpOrP100;

        for (String folderName : folderNames) {
            for (Integer runId : getRunIdsFromDatabase(folderName)) {

                gcpOrP100 = folderName.contains("GCP") ? "GCP" : "P100";

                output.add(String.format(gctDownloadUrl2, gcpOrP100, runId, gcpOrP100, runId, ifProcessed));
            }
        }
        return output;
    }



    private List<Integer> getRunIdsFromDatabase(String folderName) throws IOException, CommandException {
        ArrayList<Integer> runIds = new ArrayList<Integer>();
        Connection cn = new Connection(panoramaConnectionUrl);

        SelectRowsCommand cmd = new SelectRowsCommand("targetedms", "runs");
        cmd.getColumns().addAll(Arrays.asList("Id", "Description", "Status"));

        SelectRowsResponse response = cmd.execute(cn, folderName);
        List<Map<String, Object>> rows = response.getRows();

        for (Map<String, Object> row : rows) {
            if(row.get("Description").toString().contains("QC")) continue; // skip Quality Control files
            if(row.get("Status") != null && row.get("Status").toString().contains("failed")) continue; // skip failed imports
            runIds.add((Integer) row.get("Id"));
        }
  //*****************************

        ArrayList<Integer> runIds2 = new ArrayList<Integer>();
        runIds2.add(31527);
        //runIds2.add(9024);

//25241
//runIds2.add(25241);
//runIds2.add(25265);
//runIds2.add(16048);
//runIds2.add(16046);
//runIds2.add(16047);
//runIds2.add(16141);

//        runIds2.add(8772);
//        runIds2.add(8771);

//runIds2.add(19772);
               
//runIds2.add(20677);
//runIds2.add(25454);
//runIds2.add(25461);
//runIds2.add(20948);
//runIds2.add(24961);
//runIds2.add(25011);
//runIds2.add(25256);
//runIds2.add(4420);
//runIds2.add(6808);
//runIds2.add(9024);
//runIds2.add(4425);
//runIds2.add(4424);
//runIds2.add(4442);
//runIds2.add(4421);
//runIds2.add(4426);
//runIds2.add(4422);
//runIds2.add(4431);
//runIds2.add(4423);
//runIds2.add(4435);
//runIds2.add(4440);
//runIds2.add(4520);
//runIds2.add(9202);
//runIds2.add(9206);
//runIds2.add(8064);
//runIds2.add(8411);
//runIds2.add(13243);
//runIds2.add(14018);
//runIds2.add(4403);
//runIds2.add(8063);
//runIds2.add(12200);
//runIds2.add(8772);
//runIds2.add(14050);
//runIds2.add(13068);
//runIds2.add(13923);
//runIds2.add(16051);
//runIds2.add(16052);
//runIds2.add(16355);
//runIds2.add(20406);
//runIds2.add(20449);
//runIds2.add(20875);
//runIds2.add(3106);
//runIds2.add(3030);
//runIds2.add(3281);
//runIds2.add(3280);
//runIds2.add(3031);
//runIds2.add(3063);
//runIds2.add(3064);
//runIds2.add(3083);
//runIds2.add(3085);
//runIds2.add(4165);
//runIds2.add(6650);
//runIds2.add(6874);
//runIds2.add(6872);
//runIds2.add(6873);
//runIds2.add(9209);
//runIds2.add(7988);
//runIds2.add(8060);
//runIds2.add(12201);
//
//runIds2.add(8512);
//
//runIds2.add(13921);
//
//runIds2.add(13069);
//
//runIds2.add(12202);
//
//runIds2.add(8771);
//
//runIds2.add(14026);
        System.out.println("runIds");
        System.out.println(runIds);


        return runIds;

        //*****************************
        //return runIds;
        
    }

    public HashMap<String, Integer> getPeptideIdsFromJSON(List<String> peptideIds, AssayType assayType, int runId) throws IOException {

        HashMap<String,Integer> output;

        StringBuilder sb = new StringBuilder();

        for(String peptideId : peptideIds){
            String escapedPeptideId = peptideId.replaceAll("\\+", "%2B");
            if(sb.length()==0){
                sb.append(escapedPeptideId);
            }else{
                sb.append(";").append(escapedPeptideId);
            }
        }

        String stepOne = String.format(peptideInternalIdsUrl, assayType, sb.toString(), runId);


        output= UtilsParse.parsePeptideNumbers(stepOne, peptideIds);


        return output;
    }

    public String getRunIdLink(GctFile gctFile){

        AssayType assayType = gctFile.getAssayType();
        int runId = gctFile.getRunId();

        return String.format(runIdUrl,assayType,runId);
    }

    public String getChromatogramUrl(AssayType assayType, Integer peptide, String replicateId) {
        return String.format(chromatogramUrl, assayType, peptide, replicateId);
    }


    /**
     * Insert assay name into Panorama API URL template for peptide annotations.
     * @param assayType
     * @return
     */
    public String getPeptideJsonUrl(AssayType assayType) {
        return String.format(peptideAnnotationsUrl, assayType);
    }

    /**
     * Insert assay name into Panorama API URL template for replicate annotations.
     * @param assayType
     * @return
     */
    public String getReplicateJsonUrl(AssayType assayType) {
        return String.format(replicateAnnotationsUrl, assayType);
    }
}
