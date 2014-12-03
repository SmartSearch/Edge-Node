%% Event Classifier - Angathan FRANCIS
% Provides the Euclidian distance between two signals of the same length


function [dis] = dist(desired, ref)

    Nobs = length(desired);
    dis = 0;
    
    for i = 1 : Nobs,
        
        dis = dis + (desired(i) - ref(i))^2;
        
    end
    
end