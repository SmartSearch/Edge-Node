%% Event Classifier - Angathan FRANCIS
% Provides the minimum distance between the desired signal and a reference 
% one of the same length

function [mindist] = distoref(desired, ref)

    Nobs = length(desired);
    Nref = length(ref);
    mindist = 10;

    for i = 1 : Nref - Nobs + 1,
        
        mindist = min(mindist, dist(ref(i : i + Nobs - 1), desired));
        
    end
    
end