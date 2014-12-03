%% Event Classifier - Angathan FRANCIS
% Creating Reference Time Series

function [Event, Nonevent, Apple] = refsig(Nsmooth, alpha1, alpha2)

    % Initialization
    [e, ne, apple] = cumvol();
    [n, m] = size(e);
    Be = zeros(n, m);
    Bne = zeros(n, m);
    Ce = zeros(n, m);
    Cne = zeros(n, m);

    % Tweet Rate Computation
    for i = 2 : m,

        Be(:, i) = e(:, i) - e(:, i-1);
        Bne(:, i) = ne(:, i) - ne(:, i-1);

    end

    % Baseline Normalization
    for j = 1 : n,
        
        dive = sum(Be(j, :));
        divne = sum(Bne(j, :));
        
        for i = 1 : m,

            Be(j, i) = (Be(j, i)/dive)^(alpha1);
            Bne(j, i) = (Bne(j, i)/divne)^(alpha2);
            
        end
        
    end

    % Spike Normalization
    for i = 2 : m,

        Ce(:, i) = abs(Be(:, i) - Be(:, i-1)).^(1.2);
        Cne(:, i) = abs(Bne(:, i) - Bne(:, i-1)).^(1.2);

    end

    % Smoothing
    for i = 1 : n,

        Pe(i, :) = conv(Ce(i, :), ones(1, Nsmooth));
        Pne(i, :) = conv(Cne(i, :), ones(1, Nsmooth));

    end
    
    % Log
    %Event = log10(Pe);
    %Nonevent = log10(Pne);
    Event = Pe;
    Nonevent = Pne;
    
%% Apple Processing
for i = 2 : length(apple),
    B(i) = apple(i) - apple(i-1);
end

B = B/sum(B);

for i = 2 : length(B),
    BB(:, i) = abs(B(:, i) - Be(:, i-1));
end

for i = 2 : length(BB),
    C(i) = abs(BB(i) - BB(i-1))^(1.2);
end

P = conv(C, ones(1, 80));
%Apple = log10(P);
Apple = P;
end